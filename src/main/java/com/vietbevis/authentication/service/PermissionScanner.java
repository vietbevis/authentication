package vn.vietbevis.common;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import vn.vietbevis.dto.request.permission.EndpointInfo;
import vn.vietbevis.service.PermissionService;
import vn.vietbevis.service.RoleService;

/**
 * Component to scan all endpoints and synchronize permissions on application startup
 */
@Component
public class EndpointScanner implements CommandLineRunner {

  private final RequestMappingHandlerMapping handlerMapping;
  private final PermissionService permissionService;
  private final RoleService roleService;

  @Autowired
  public EndpointScanner(
      @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
      PermissionService permissionService,
      RoleService roleService
  ) {
    this.handlerMapping = handlerMapping;
    this.permissionService = permissionService;
    this.roleService = roleService;
  }

  @Override
  @Transactional
  public void run(String... args) {
    List<EndpointInfo> endpoints = scanEndpoints();
    permissionService.syncPermissionsFromEndpoints(endpoints);
    roleService.createOrUpdateBaseRole(BaseRole.ADMIN);
  }

  /**
   * Scan all endpoints in the application
   *
   * @return List of endpoint information
   */
  private List<EndpointInfo> scanEndpoints() {
    List<EndpointInfo> endpoints = new ArrayList<>();

    handlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
      // Skip actuator endpoints and other system endpoints
      if (shouldSkipEndpoint(handlerMethod)) {
        return;
      }

      assert requestMappingInfo.getPathPatternsCondition() != null;
      requestMappingInfo.getPathPatternsCondition().getPatterns().forEach(
          pattern -> requestMappingInfo.getMethodsCondition().getMethods()
              .forEach(method -> endpoints.add(EndpointInfo.builder()
                  .path(pattern.getPatternString())
                  .method(convertToHttpMethod(method))
                  .handlerMethod(handlerMethod.toString())
                  .build())));
    });

    return endpoints;
  }

  /**
   * Skip certain endpoints that should not have permissions
   *
   * @param handlerMethod Handler method
   * @return true if endpoint should be skipped
   */
  private boolean shouldSkipEndpoint(HandlerMethod handlerMethod) {
    // Skip Spring Boot Actuator endpoints
    String packageName = handlerMethod.getBeanType().getPackage().getName();
    return packageName.startsWith("org.springframework") || packageName.startsWith("org.springdoc")
        || packageName.contains("actuator")
        || handlerMethod.getBeanType().getSimpleName().contains("Error");
  }

  /**
   * Convert Spring RequestMethod to our HttpMethod enum
   *
   * @param method Spring RequestMethod
   * @return HttpMethod
   */
  private HttpMethod convertToHttpMethod(
      RequestMethod method) {
    return HttpMethod.valueOf(method.name());
  }
}
