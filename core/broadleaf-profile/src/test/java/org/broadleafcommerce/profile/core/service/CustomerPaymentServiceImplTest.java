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

import org.broadleafcommerce.profile.core.dao.CustomerPaymentDaoImpl;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 *
 * @author aapowers
 *
 */
public class CustomerPaymentServiceImplTest {
    @Mock
    private CustomerPaymentDaoImpl customerPaymentDaoMock;

    @Mock
    private CustomerServiceImpl customerServiceMock;

    @InjectMocks
    private CustomerPaymentServiceImpl customerPaymentService;

    @Before
    public void setUp() {
        customerPaymentService = new CustomerPaymentServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveCustomerPayment() {
        CustomerPayment customerPayment = new CustomerPaymentImpl();
        when(customerPaymentDaoMock.save(customerPayment)).thenReturn(customerPayment);
        CustomerPayment returnedCustomerPayment = customerPaymentService.saveCustomerPayment(customerPayment);
        Assert.assertEquals(customerPayment, returnedCustomerPayment);
    }

    @Test
    public void testReadCustomerPaymentsByCustomerId() {
        long customerId = 123L;
        List<CustomerPayment> customerPayments = Arrays.asList(new CustomerPaymentImpl(), new CustomerPaymentImpl());
        when(customerPaymentDaoMock.readCustomerPaymentsByCustomerId(customerId)).thenReturn(customerPayments);
        List<CustomerPayment> returnedCustomerPayments = customerPaymentService.readCustomerPaymentsByCustomerId(customerId);
        Assert.assertEquals(customerPayments, returnedCustomerPayments);
    }

    @Test
    public void testReadCustomerPaymentById() {
        long customerPaymentId = 123L;
        CustomerPayment customerPayment = new CustomerPaymentImpl();
        when(customerPaymentDaoMock.readCustomerPaymentById(customerPaymentId)).thenReturn(customerPayment);
        CustomerPayment returnedCustomerPayment = customerPaymentService.readCustomerPaymentById(customerPaymentId);
        Assert.assertEquals(customerPayment, returnedCustomerPayment);
    }

    @Test
    public void testReadCustomerPaymentByToken() {
        String customerPaymentToken = "asdf";
        CustomerPayment customerPayment = new CustomerPaymentImpl();
        when(customerPaymentDaoMock.readCustomerPaymentByToken(customerPaymentToken)).thenReturn(customerPayment);
        CustomerPayment returnedCustomerPayment = customerPaymentService.readCustomerPaymentByToken(customerPaymentToken);
        Assert.assertEquals(customerPayment, returnedCustomerPayment);
    }

    @Test
    public void testDeleteCustomerPaymentById() {
        long customerPaymentId = 1L;
        doNothing().when(customerPaymentDaoMock).deleteCustomerPaymentById(customerPaymentId);
        customerPaymentService.deleteCustomerPaymentById(customerPaymentId);
    }

    @Test
    public void testDeleteCustomerPaymentByTaken() {
        String customerPaymentToken = "asdf";
        doNothing().when(customerPaymentDaoMock).deleteCustomerPaymentByToken(customerPaymentToken);
        customerPaymentService.deleteCustomerPaymentByToken(customerPaymentToken);
    }

    @Test
    public void testCreate() {
        String customerPaymentToken = "asdf";
        CustomerPayment customerPayment = new CustomerPaymentImpl();
        when(customerPaymentDaoMock.create()).thenReturn(customerPayment);
        customerPaymentService.create();
    }

    @Test
    public void testFindDefaultPaymentForCustomer_nullCustomer() {
        Customer customer = null;
        CustomerPayment customerPayment = customerPaymentService.findDefaultPaymentForCustomer(customer);
        Assert.assertNull(customerPayment);
    }

    @Test
    public void testFindDefaultPaymentForCustomer_noDefault() {
        long customerId = 1L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        CustomerPayment customerPayment1 = new CustomerPaymentImpl();
        CustomerPayment customerPayment2 = new CustomerPaymentImpl();
        List<CustomerPayment> customerPayments = Arrays.asList(customerPayment1, customerPayment2);
        when(customerPaymentDaoMock.readCustomerPaymentsByCustomerId(customerId)).thenReturn(customerPayments);
        CustomerPayment customerPayment = customerPaymentService.findDefaultPaymentForCustomer(customer);
        Assert.assertNull(customerPayment);
    }

    @Test
    public void testFindDefaultPaymentForCustomer_withDefault() {
        long customerId = 1L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        CustomerPayment customerPayment1 = new CustomerPaymentImpl();
        CustomerPayment customerPayment2 = new CustomerPaymentImpl();
        customerPayment2.setIsDefault(true);
        List<CustomerPayment> customerPayments = Arrays.asList(customerPayment1, customerPayment2);
        when(customerPaymentDaoMock.readCustomerPaymentsByCustomerId(customerId)).thenReturn(customerPayments);
        CustomerPayment returnedCustomerPayment = customerPaymentService.findDefaultPaymentForCustomer(customer);
        Assert.assertEquals(customerPayment2, returnedCustomerPayment);
    }

    @Test
    public void testSetAsDefaultPayment_noDefault() {
        long customerId = 1L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        CustomerPayment customerPayment = new CustomerPaymentImpl();
        customerPayment.setCustomer(customer);
        when(customerPaymentDaoMock.readCustomerPaymentsByCustomerId(customerId)).thenReturn(new ArrayList<>());
        when(customerPaymentDaoMock.save(customerPayment)).thenReturn(customerPayment);
        CustomerPayment returnedCustomerPayment = customerPaymentService.setAsDefaultPayment(customerPayment);
        Assert.assertTrue(returnedCustomerPayment.isDefault());
    }

    @Test
    public void testSetAsDefaultPayment_withDefault() {
        long customerId = 1L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        CustomerPayment customerPayment1 = new CustomerPaymentImpl();
        CustomerPayment customerPayment2 = new CustomerPaymentImpl();
        customerPayment2.setIsDefault(true);
        customerPayment1.setCustomer(customer);
        List<CustomerPayment> customerPayments = Arrays.asList(customerPayment1, customerPayment2);
        when(customerPaymentDaoMock.readCustomerPaymentsByCustomerId(customerId)).thenReturn(customerPayments);
        when(customerPaymentDaoMock.save(customerPayment2)).thenReturn(customerPayment2);
        when(customerPaymentDaoMock.save(customerPayment1)).thenReturn(customerPayment1);
        CustomerPayment returnedCustomerPayment = customerPaymentService.setAsDefaultPayment(customerPayment1);
        Assert.assertTrue(returnedCustomerPayment.isDefault());
    }

    @Test
    public void testClearDefaultPaymentStatus_withDefault() {
        long customerId = 1L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        CustomerPayment customerPayment1 = new CustomerPaymentImpl();
        CustomerPayment customerPayment2 = new CustomerPaymentImpl();
        customerPayment2.setIsDefault(true);
        List<CustomerPayment> customerPayments = Arrays.asList(customerPayment1, customerPayment2);
        when(customerPaymentDaoMock.save(customerPayment2)).thenReturn(customerPayment2);
        when(customerPaymentDaoMock.readCustomerPaymentsByCustomerId(customerId)).thenReturn(customerPayments);
        customerPaymentService.clearDefaultPaymentStatus(customer);
    }

    @Test
    public void testClearDefaultPaymentStatus_noDefault() {
        long customerId = 1L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        CustomerPayment customerPayment1 = new CustomerPaymentImpl();
        CustomerPayment customerPayment2 = new CustomerPaymentImpl();
        List<CustomerPayment> customerPayments = Arrays.asList(customerPayment1, customerPayment2);
        when(customerPaymentDaoMock.readCustomerPaymentsByCustomerId(customerId)).thenReturn(customerPayments);
        customerPaymentService.clearDefaultPaymentStatus(customer);
    }

    @Test
    public void testDeleteCustomerPaymentFromCustomer() {
        long customerId = 1L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        CustomerPayment customerPayment1 = new CustomerPaymentImpl();
        CustomerPayment customerPayment2 = new CustomerPaymentImpl();
        customerPayment1.setId(1L);
        customerPayment2.setId(2L);
        ArrayList<CustomerPayment> customerPayments = new ArrayList<>();
        customerPayments.add(customerPayment1);
        customerPayments.add(customerPayment2);
        customer.setCustomerPayments(customerPayments);
        when(customerServiceMock.saveCustomer(customer)).thenReturn(customer);
        Customer returnedCustomer = customerPaymentService.deleteCustomerPaymentFromCustomer(customer, customerPayment2);
        Assert.assertEquals(1, returnedCustomer.getCustomerPayments().size());
        Assert.assertEquals(customerPayment1, returnedCustomer.getCustomerPayments().get(0));
    }
}
