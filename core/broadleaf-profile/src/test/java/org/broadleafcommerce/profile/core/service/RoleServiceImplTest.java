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

import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.CustomerRole;
import org.broadleafcommerce.profile.core.domain.CustomerRoleImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 *
 * @author aapowers
 *
 */
public class RoleServiceImplTest {
    @Mock
    private RoleDao roleDaoMock;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Before
    public void setUp() {
        roleService = new RoleServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindCustomerRolesByCustomerId() {
        long customerId = 12345L;
        List<CustomerRole> customerRoles = Arrays.asList(new CustomerRoleImpl(), new CustomerRoleImpl());
        // mock the service call that would call the database
        when(roleDaoMock.readCustomerRolesByCustomerId(customerId)).thenReturn(customerRoles);
        List<CustomerRole> returnedCustomerRoles = roleService.findCustomerRolesByCustomerId(customerId);
        Assert.assertEquals(customerRoles, returnedCustomerRoles);
    }
}
