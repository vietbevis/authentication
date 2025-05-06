package com.vietbevis.authentication.security;

import com.vietbevis.authentication.common.UserStatus;
import com.vietbevis.authentication.entity.UserEntity;
import com.vietbevis.authentication.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends DefaultOAuth2UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + email));
    return UserPrincipal.create(user);
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);

    String email = oauth2User.getAttribute("email");
    String name = oauth2User.getAttribute("name");
    String picture = oauth2User.getAttribute("picture");
    String provider = userRequest.getClientRegistration().getRegistrationId();
    String providerId = oauth2User.getAttribute("sub");
    Optional<UserEntity> user = userRepository.findByEmail(email);

    if (user.isEmpty()) {
      UserEntity userEntity = UserEntity.builder()
          .email(email)
          .fullName(name)
          .password("")
          .googleId(providerId)
          .status(UserStatus.ACTIVE)
          .build();
      userRepository.save(userEntity);
      return UserPrincipal.create(userEntity, oauth2User.getAttributes());
    } else {
      UserEntity existingUser = user.get();
      return UserPrincipal.create(existingUser, oauth2User.getAttributes());
    }
  }
}
