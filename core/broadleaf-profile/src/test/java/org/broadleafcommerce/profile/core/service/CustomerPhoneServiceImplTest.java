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

import org.broadleafcommerce.profile.core.dao.CustomerPhoneDao;
import org.broadleafcommerce.profile.core.domain.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.calls;

/**
 *
 * @author aapowers
 *
 */
public class CustomerPhoneServiceImplTest {
    @Mock
    private CustomerPhoneDao customerPhoneDaoMock;

    @InjectMocks
    private CustomerPhoneServiceImpl customerPhoneService;

    @Before
    public void setUp() {
        customerPhoneService = new CustomerPhoneServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveCustomerPhone_WithOnlyPhone() {
        long customerId = 1L;
        CustomerPhone customerPhone = new CustomerPhoneImpl();
        customerPhone.setPhone(new PhoneImpl());
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        customerPhone.setCustomer(customer);
        when(customerPhoneDaoMock.readActiveCustomerPhonesByCustomerId(customerId)).thenReturn(new ArrayList<>());
        when(customerPhoneDaoMock.save(customerPhone)).thenReturn(customerPhone);
        CustomerPhone returnedCustomerPhone = customerPhoneService.saveCustomerPhone(customerPhone);
        Assert.assertEquals(customerPhone, returnedCustomerPhone);
        InOrder inOrder = inOrder(customerPhoneDaoMock);
        inOrder.verify(customerPhoneDaoMock, calls(1)).readActiveCustomerPhonesByCustomerId(customerId);
        inOrder.verify(customerPhoneDaoMock, calls(1)).save(customerPhone);
    }

    @Test
    public void testSaveCustomerPhone_WithExistingPhones() {
        // set up new phone
        long customerId = 1L;
        CustomerPhone customerPhone = new CustomerPhoneImpl();
        Phone phone = new PhoneImpl();
        phone.setDefault(true);
        customerPhone.setPhone(phone);
        customerPhone.setId((long) 1);
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        customerPhone.setCustomer(customer);
        // set up existing phone
        CustomerPhone existingCustomerPhone = new CustomerPhoneImpl();
        Phone existingPhone = new PhoneImpl();
        existingCustomerPhone.setId((long) 2);
        existingPhone.setDefault(true);
        existingCustomerPhone.setPhone(existingPhone);

        when(customerPhoneDaoMock.readActiveCustomerPhonesByCustomerId(customerId)).thenReturn(Collections.singletonList(existingCustomerPhone));
        when(customerPhoneDaoMock.save(existingCustomerPhone)).thenReturn(existingCustomerPhone);
        when(customerPhoneDaoMock.save(customerPhone)).thenReturn(customerPhone);
        CustomerPhone returnedCustomerPhone = customerPhoneService.saveCustomerPhone(customerPhone);
        Assert.assertTrue(returnedCustomerPhone.getPhone().isDefault());
        Assert.assertEquals(customerPhone, returnedCustomerPhone);
        InOrder inOrder = inOrder(customerPhoneDaoMock);
        inOrder.verify(customerPhoneDaoMock, calls(1)).readActiveCustomerPhonesByCustomerId(customerId);
        inOrder.verify(customerPhoneDaoMock, calls(1)).save(existingCustomerPhone);
        inOrder.verify(customerPhoneDaoMock, calls(1)).save(customerPhone);
    }

    @Test
    public void testReadActiveCustomerPhonesByCustomerId() {
        Long customerId = 2L;
        List<CustomerPhone> customerPhones = Arrays.asList(new CustomerPhoneImpl(), new CustomerPhoneImpl());
        when(customerPhoneDaoMock.readActiveCustomerPhonesByCustomerId(customerId)).thenReturn(customerPhones);
        List<CustomerPhone> returnedCustomerPhones = customerPhoneService.readActiveCustomerPhonesByCustomerId(customerId);
        Assert.assertEquals(customerPhones, returnedCustomerPhones);
        verify(customerPhoneDaoMock, times(1)).readActiveCustomerPhonesByCustomerId(customerId);
    }

    @Test
    public void testReadCustomerPhoneById() {
        Long phoneId = 2L;
        CustomerPhone customerPhone = new CustomerPhoneImpl();
        customerPhone.setId(phoneId);
        // mock the service call that would call the database
        when(customerPhoneDaoMock.readCustomerPhoneById(phoneId)).thenReturn(customerPhone);
        CustomerPhone returnedCustomerPhone = customerPhoneService.readCustomerPhoneById(phoneId);
        Assert.assertEquals(customerPhone, returnedCustomerPhone);
        verify(customerPhoneDaoMock, times(1)).readCustomerPhoneById(phoneId);
    }

    @Test
    public void testMakeCustomerPhoneDefault() {
        Long phoneId = 2L;
        Long customerId = 12L;
        doNothing().when(customerPhoneDaoMock).makeCustomerPhoneDefault(phoneId, customerId);
        customerPhoneService.makeCustomerPhoneDefault(phoneId, customerId);
        verify(customerPhoneDaoMock, times(1)).makeCustomerPhoneDefault(phoneId, customerId);
    }

    @Test
    public void testDeleteCustomerPhoneById() {
        Long phoneId = 2L;
        doNothing().when(customerPhoneDaoMock).deleteCustomerPhoneById(phoneId);
        customerPhoneService.deleteCustomerPhoneById(phoneId);
        verify(customerPhoneDaoMock, times(1)).deleteCustomerPhoneById(phoneId);
    }

    @Test
    public void testFindDefaultCustomerPhone() {
        Long customerId = 12L;
        CustomerPhone customerPhone = new CustomerPhoneImpl();
        when(customerPhoneDaoMock.findDefaultCustomerPhone(customerId)).thenReturn(customerPhone);
        CustomerPhone returnedCustomerPhone = customerPhoneService.findDefaultCustomerPhone(customerId);
        Assert.assertEquals(customerPhone, returnedCustomerPhone);
        verify(customerPhoneDaoMock, times(1)).findDefaultCustomerPhone(customerId);
    }

    @Test
    public void testReadAllCustomerPhonesByCustomerId() {
        Long customerId = 12L;
        List<CustomerPhone> customerPhones = Arrays.asList(new CustomerPhoneImpl(), new CustomerPhoneImpl());
        when(customerPhoneDaoMock.readAllCustomerPhonesByCustomerId(customerId)).thenReturn(customerPhones);
        List<CustomerPhone> returnedCustomerPhones = customerPhoneService.readAllCustomerPhonesByCustomerId(customerId);
        Assert.assertEquals(customerPhones, returnedCustomerPhones);
        verify(customerPhoneDaoMock, times(1)).readAllCustomerPhonesByCustomerId(customerId);
    }

    @Test
    public void testCreate() {
        CustomerPhone customerPhone = new CustomerPhoneImpl();
        when(customerPhoneDaoMock.create()).thenReturn(customerPhone);
        CustomerPhone returnedCustomerPhone = customerPhoneService.create();
        Assert.assertEquals(customerPhone, returnedCustomerPhone);
        verify(customerPhoneDaoMock, times(1)).create();
    }
}
