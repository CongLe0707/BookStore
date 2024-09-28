    package com.example.WebBanSach.services;

    import com.example.WebBanSach.entity.Role;
    import com.example.WebBanSach.entity.User;
    import com.example.WebBanSach.repository.IRoleRepository;
    import com.example.WebBanSach.repository.IUserRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;

    import org.springframework.stereotype.Service;


    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;


    @Service
    public class UserServices {

        private final IUserRepository userRepository;
        private final IRoleRepository roleRepository;


        @Autowired
        public UserServices(IUserRepository userRepository, IRoleRepository roleRepository) {
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;

            createDefaultRoles();
        }

        @Transactional
        public void save(User user) {
            //user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            Role userRole = roleRepository.findByName("USER");
            if (userRole == null) {
                throw new RuntimeException("Role not found: USER");
            }
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);
        }


        @Transactional
        private void createDefaultRoles() {
            if (roleRepository.findByName("USER") == null) {
                Role userRole = new Role();
                userRole.setName("USER");
                roleRepository.save(userRole);
            }

            if (roleRepository.findByName("ADMIN") == null) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }
        }
        public User getUserById(Long id) {
            return userRepository.findById((long) Math.toIntExact(id)).orElse(null);
        }


        public List<User> findAllUsers() {
            return userRepository.findAll();
        }


        @Transactional
        public User findByUsername(String username) {
            return userRepository.findByUsername(username);
        }
    }