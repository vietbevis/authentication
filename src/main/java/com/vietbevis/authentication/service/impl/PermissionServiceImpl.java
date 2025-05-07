package vn.vietbevis.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.vietbevis.common.HttpMethod;
import vn.vietbevis.dto.mapper.PermissionMapper;
import vn.vietbevis.dto.request.permission.EndpointInfo;
import vn.vietbevis.dto.request.permission.UpdatePermissionDTO;
import vn.vietbevis.dto.response.permission.PermissionRes;
import vn.vietbevis.entity.PermissionEntity;
import vn.vietbevis.exception.NotFoundException;
import vn.vietbevis.repository.PermissionRepository;
import vn.vietbevis.service.PermissionService;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;
  private final PermissionMapper permissionMapper;


  @Override
  public PermissionRes findById(Long id) {
    return this.permissionMapper.toPermissionRes(
        this.permissionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Permission not found")));
  }

  @Transactional
  public void syncPermissionsFromEndpoints(List<EndpointInfo> endpoints) {
    for (EndpointInfo endpoint : endpoints) {
      String permissionName = generatePermissionName(endpoint.getMethod(), endpoint.getPath());
      String resource = extractResourceFromPath(endpoint.getPath());

      Optional<PermissionEntity> existingPermission = permissionRepository.findByApiPathAndMethod(
          endpoint.getPath(), endpoint.getMethod());

      if (existingPermission.isEmpty()) {
        PermissionEntity newPermission = PermissionEntity.builder()
            .name(permissionName)
            .description("Auto-generated permission for " + endpoint.getPath())
            .apiPath(endpoint.getPath())
            .method(endpoint.getMethod())
            .resource(resource)
            .build();

        permissionRepository.save(newPermission);
      }
    }
  }

  @Override
  public List<PermissionRes> findAllByKeyword(String keyword) {
    return this.permissionMapper.toPermissionResList(
        this.permissionRepository.findAllByKeyword(keyword));
  }

  @Override
  public PermissionRes updatePermission(Long id, UpdatePermissionDTO updatePermissionDTO) {
    PermissionEntity existsPermission = this.permissionRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Permission not found"));

    this.permissionMapper.updateEntityFromDTO(updatePermissionDTO, existsPermission);

    return this.permissionMapper.toPermissionRes(this.permissionRepository.save(existsPermission));
  }

  /**
   * Generate permission name from method and path
   *
   * @param method HTTP method
   * @param path   API path
   * @return Generated permission name
   */
  private String generatePermissionName(HttpMethod method, String path) {
    String resource = extractResourceFromPath(path);
    String action = determineActionFromMethod(method);

    return action + "_" + resource.toUpperCase();
  }

  /**
   * Extract resource name from API path
   *
   * @param path API path
   * @return Resource name
   */
  private String extractResourceFromPath(String path) {
    // Remove leading slashes and split by slash
    String[] pathParts = path.replaceAll("^/+", "").split("/");

    // First part is usually the resource name
    if (pathParts.length > 1) {
      return pathParts[1].toLowerCase();
    }

    return "general";
  }

  /**
   * Determine action based on HTTP method
   *
   * @param method HTTP method
   * @return Action name
   */
  private String determineActionFromMethod(HttpMethod method) {
    return switch (method) {
      case GET -> "VIEW";
      case POST -> "CREATE";
      case PUT, PATCH -> "UPDATE";
      case DELETE -> "DELETE";
      default -> method.name();
    };
  }
}
