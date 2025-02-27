package kr.ac.dankook.VettAuthServer.config.principal;

import kr.ac.dankook.VettAuthServer.entity.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class PrincipalDetails implements UserDetails{

    private Member member;

    public PrincipalDetails(Member member){
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        member.getRoleList().forEach(role -> authorities.add(() -> role));
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserId();
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public boolean isEnabled() {
        return true;
    }
}