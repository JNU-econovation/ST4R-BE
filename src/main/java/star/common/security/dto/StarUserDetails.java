package star.common.security.dto;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import star.member.dto.MemberInfoDTO;
import star.member.model.entity.Role;

@RequiredArgsConstructor
public class StarUserDetails implements UserDetails {
    private static final String ROLE_PREFIX = "ROLE_";

    private final MemberInfoDTO memberInfo;

    public MemberInfoDTO getMemberInfoDTO() {
        return memberInfo;
    }

    @Override public String getUsername() { return memberInfo.email().value(); }
    @Override public String getPassword() { return "Social"; }

    //사용자의 역할
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = memberInfo.role();
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()));
    }

    //계정이 만료 됐는지 ex) 구독제
    @Override public boolean isAccountNonExpired() { return true; }

    //계정이 잠겼는지
    @Override public boolean isAccountNonLocked() { return true; }

    //비밀번호 만료 여부
    @Override public boolean isCredentialsNonExpired() { return true; }

    //계정이 활성화 됐는지
    @Override public boolean isEnabled() { return true; }
}