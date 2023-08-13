package com.shadoww.BookLibraryApp.config;


import com.shadoww.BookLibraryApp.models.user.Privilege;
import com.shadoww.BookLibraryApp.models.user.Role;
import com.shadoww.BookLibraryApp.services.PeopleDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

//@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {


    private PeopleDetailsService peopleDetailsService;


    @Autowired
    public SecurityConfiguration(PeopleDetailsService userDetailsImp) {
        this.peopleDetailsService = userDetailsImp;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        http
                    .requestCache(cache->cache.requestCache(requestCache))
//                    .authorizeHttpRequests(auth->{
//                        System.out.println("Privileges:");
//                        for(var pr : Privilege.values()) {
//                            System.out.println(pr);
//                            auth.requestMatchers(pr.getUrl()).hasAuthority(pr.name());
//                        }
//
//                    })
//                    .authorizeHttpRequests().anyRequest().permitAll()
//                .and()
                    .formLogin()
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/auth/login?error")
//                        .permitAll()
                .and()
                    .logout().logoutUrl("/auth/logout").logoutSuccessUrl("/auth/login");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public AuthenticationManagerBuilder authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        UserDetails user = User.builder()
                .username("super_admin")
                .password(passwordEncoder().encode("super_Dungeon141tw@"))
//                .authorities(Role.SUPER_ADMIN.name())
                .roles(Role.SUPER_ADMIN.name())
                .build();

        auth.inMemoryAuthentication().withUser(user);
        auth.userDetailsService(peopleDetailsService);
        return auth;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_SUPER_ADMIN > ROLE_ADMIN > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
