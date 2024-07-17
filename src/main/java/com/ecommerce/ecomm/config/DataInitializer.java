package com.ecommerce.ecomm.config;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ecommerce.ecomm.entities.Permission;
import com.ecommerce.ecomm.entities.Role;
import com.ecommerce.ecomm.repository.PermissionRepository;
import com.ecommerce.ecomm.repository.RoleRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
    private PermissionRepository permissionRepository;

	@Override
	public void run(String... args) throws Exception {
		if (roleRepository.findByName("ADMIN") == null) {
			Role adminRole = new Role();
			adminRole.setName("ADMIN");
			roleRepository.save(adminRole);
		}

		if (roleRepository.findByName("USER") == null) {
			Role userRole = new Role();
			userRole.setName("USER");
			roleRepository.save(userRole);
		}
	}

    @PostConstruct
    public void initializeRolesAndPermissions() {
        // Check if roles already exist
        if (roleRepository.findByName("USER_ROLE") == null) {
            Role userRole = new Role();
            userRole.setName("USER_ROLE");
            userRole.setPermissions(new HashSet<>());

            // Create permissions if they don't exist
            Permission readPrivileges = permissionRepository.findByName("READ_PRIVILEGES");
            if (readPrivileges == null) {
                readPrivileges = new Permission();
                readPrivileges.setName("READ_PRIVILEGES");
                permissionRepository.save(readPrivileges);
            }

            Permission writePrivileges = permissionRepository.findByName("WRITE_PRIVILEGES");
            if (writePrivileges == null) {
                writePrivileges = new Permission();
                writePrivileges.setName("WRITE_PRIVILEGES");
                permissionRepository.save(writePrivileges);
            }

            // Add permissions to the role
            userRole.getPermissions().add(readPrivileges);
            userRole.getPermissions().add(writePrivileges);

            // Save the role
            roleRepository.save(userRole);
        }
    }
}