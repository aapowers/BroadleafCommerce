/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.when;

public class UserDetailsServiceImplTest {
    @Mock
    private CustomerServiceImpl customerServiceMock;

    @Mock
    private RoleServiceImpl roleServiceMock;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected=UsernameNotFoundException.class)
    public void testLoadUserByUsername_NonExistentUser_ShouldThrowException() {
        when(customerServiceMock.readCustomerByUsername("user1nonexistent")).thenReturn(null);
        userDetailsService.loadUserByUsername("user1nonexistent");
    }

    @Test
    public void testLoadUserByUsername_DeactivatedUser_DoesNotNeedPasswordChange_WithRoleUser() {
        // set up user
        String username = "user2";
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("ROLE_USER");
        setUpMockCustomer(username, true, false, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertTrue(returnedUser.isCredentialsNonExpired());
        Assertions.assertFalse(returnedUser.isEnabled());
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    @Test
    public void testLoadUserByUsername_DeactivatedUser_DoesNotNeedPasswordChange_WithoutRoleUser() {
        // set up user
        String username = "user3";
        ArrayList<String> roles = new ArrayList<String>();
        setUpMockCustomer(username, true, false, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertTrue(returnedUser.isCredentialsNonExpired());
        Assertions.assertFalse(returnedUser.isEnabled());
        roles.add("ROLE_USER");
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    @Test
    public void testLoadUserByUsername_DeactivatedUser_NeedsPasswordChange_WithRoleUser() {
        // set up user
        String username = "user4";
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("ROLE_USER");
        setUpMockCustomer(username, true, true, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertFalse(returnedUser.isCredentialsNonExpired());
        Assertions.assertFalse(returnedUser.isEnabled());
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    @Test
    public void testLoadUserByUsername_DeactivatedUser_NeedsPasswordChange_WithoutRoleUser() {
        // set up user
        String username = "user5";
        ArrayList<String> roles = new ArrayList<String>();
        setUpMockCustomer(username, true, true, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertFalse(returnedUser.isCredentialsNonExpired());
        Assertions.assertFalse(returnedUser.isEnabled());
        roles.add("ROLE_USER");
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    @Test
    public void testLoadUserByUsername_ActiveUser_NeedsPasswordChange_WithRoleUser() {
        // set up user
        String username = "user6";
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("ROLE_USER");
        setUpMockCustomer(username, false, true, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertFalse(returnedUser.isCredentialsNonExpired());
        Assertions.assertTrue(returnedUser.isEnabled());
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    @Test
    public void testLoadUserByUsername_ActiveUser_NeedsPasswordChange_WithoutRoleUser() {
        // set up user
        String username = "user7";
        ArrayList<String> roles = new ArrayList<String>();
        setUpMockCustomer(username, false, true, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertFalse(returnedUser.isCredentialsNonExpired());
        Assertions.assertTrue(returnedUser.isEnabled());
        roles.add("ROLE_USER");
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    @Test
    public void testLoadUserByUsername_ActiveUser_DoesNotNeedPasswordChange_WithRoleUser() {
        // set up user
        String username = "user8";
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        setUpMockCustomer(username, false, false, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertTrue(returnedUser.isCredentialsNonExpired());
        Assertions.assertTrue(returnedUser.isEnabled());
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    @Test
    public void testLoadUserByUsername_ActiveUser_DoesNotNeedPasswordChange_WithoutRoleUser() {
        // set up user
        String username = "user9";
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_DEV");
        setUpMockCustomer(username, false, false, roles);
        // call loadByUsername and assert
        UserDetails returnedUser = userDetailsService.loadUserByUsername(username);
        Assertions.assertEquals(username, returnedUser.getUsername());
        Assertions.assertTrue(returnedUser.isCredentialsNonExpired());
        Assertions.assertTrue(returnedUser.isEnabled());
        roles.add("ROLE_USER");
        Assertions.assertEquals(Arrays.toString(roles.toArray()), Arrays.toString(returnedUser.getAuthorities().toArray()));
    }

    /**
     * Sets up the mocked database calls involving Customer
     * @param username String
     * @param deactivated boolean
     * @param passwordChangeRequired boolean
     * @param roleStrings ArrayList<String>
     */
    private void setUpMockCustomer(String username, boolean deactivated, boolean passwordChangeRequired, ArrayList<String> roleStrings)
    {
        Customer customer = new CustomerImpl();
        customer.setId((long) 123);
        customer.setPassword("superSecurePassword");
        customer.setDeactivated(deactivated);
        customer.setPasswordChangeRequired(passwordChangeRequired);
        // mock the database lookup for the customer
        when(customerServiceMock.readCustomerByUsername(username, false)).thenReturn(customer);
        ArrayList<CustomerRole> customerRoles = getMockCustomerRoles(roleStrings);
        // mock the database lookup for the customer's roles
        when(roleServiceMock.findCustomerRolesByCustomerId(customer.getId())).thenReturn(customerRoles);
    }

    /**
     * Set up the CustomerRoles
     *
     * @param roleStrings ArrayList<String>
     * @return ArrayList<CustomerRole>
     */
    private ArrayList<CustomerRole> getMockCustomerRoles(ArrayList<String> roleStrings)
    {
        ArrayList<CustomerRole> customerRoles = new ArrayList<CustomerRole>();
        for (String roleString : roleStrings)
        {
            CustomerRoleImpl customerRole = new CustomerRoleImpl();
            RoleImpl role = new RoleImpl();
            role.setRoleName(roleString);
            customerRole.setRole(role);
            customerRoles.add(customerRole);
        }
        return customerRoles;
    }
}
