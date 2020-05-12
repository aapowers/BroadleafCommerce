/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author aapowers
 *
 */
public class CustomerUserDetailsTest {
    @Before
    public void setUp() {
    }

    @Test
    public void testInitializeWithDefaults() {
        Long id = 12345L;
        String username = "asdf";
        String password = "a1s2d3f4g5!";
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        CustomerUserDetails customerUserDetails = new CustomerUserDetails(id, username, password, grantedAuthorities);
        Assert.assertEquals(id, customerUserDetails.getId());
        Assert.assertEquals(username, customerUserDetails.getUsername());
        Assert.assertEquals(password, customerUserDetails.getPassword());
        Assert.assertTrue(customerUserDetails.isAccountNonExpired());
        Assert.assertTrue(customerUserDetails.isAccountNonLocked());
        Assert.assertTrue(customerUserDetails.isCredentialsNonExpired());
        Assert.assertTrue(customerUserDetails.isEnabled());
    }

    @Test
    public void testInitializeWithoutDefaults() {
        Long id = 12345L;
        String username = "asdf";
        String password = "a1s2d3f4g5!";
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        CustomerUserDetails customerUserDetails = new CustomerUserDetails(id, username, password, false, false, false, false, grantedAuthorities);
        Assert.assertEquals(id, customerUserDetails.getId());
        Assert.assertEquals(username, customerUserDetails.getUsername());
        Assert.assertEquals(password, customerUserDetails.getPassword());
        Assert.assertFalse(customerUserDetails.isAccountNonExpired());
        Assert.assertFalse(customerUserDetails.isAccountNonLocked());
        Assert.assertFalse(customerUserDetails.isCredentialsNonExpired());
        Assert.assertFalse(customerUserDetails.isEnabled());
    }

    @Test
    public void testWithId() {
        Long id = 12345L;
        String username = "asdf";
        String password = "a1s2d3f4g5!";
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        CustomerUserDetails customerUserDetails = new CustomerUserDetails(id, username, password, grantedAuthorities);
        Long newId = 99L;
        CustomerUserDetails returnedDetails = customerUserDetails.withId(newId);
        Assert.assertEquals(newId, returnedDetails.getId());
    }
}
