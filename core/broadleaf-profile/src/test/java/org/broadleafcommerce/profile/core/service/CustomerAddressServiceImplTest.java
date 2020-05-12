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

import org.broadleafcommerce.profile.core.dao.CustomerAddressDaoImpl;
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
public class CustomerAddressServiceImplTest {
    @Mock
    private CustomerAddressDaoImpl customerAddressDaoMock;

    @InjectMocks
    private CustomerAddressServiceImpl customerAddressService;

    @Before
    public void setUp() {
        customerAddressService = new CustomerAddressServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveCustomerAddress_WhenOnlyAddress() {
        Customer customer = new CustomerImpl();
        Long customerId = 1589L;
        customer.setId(customerId);
        CustomerAddress customerAddress = new CustomerAddressImpl();
        Address address = new AddressImpl();
        customerAddress.setCustomer(customer);
        customerAddress.setAddress(address);
        Long customerAddressId = 2L;
        customerAddress.setId(customerAddressId);
        // mock the service call that would call the database
        when(customerAddressDaoMock.readActiveCustomerAddressesByCustomerId(
                customerId
        )).thenReturn(new ArrayList<>());
        // mock the service call that would call the database
        when(customerAddressDaoMock.save(
                customerAddress
        )).thenReturn(customerAddress);
        // mock the service call that would call the database
        doNothing().when(customerAddressDaoMock).makeCustomerAddressDefault(
                customerAddressId,
                customerId
        );
        CustomerAddress returnedCustomerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        Assert.assertEquals(customerAddress, returnedCustomerAddress);
    }

    @Test
    public void testSaveCustomerAddress_WhenExistingAddresses() {
        Customer customer = new CustomerImpl();
        Long customerId = 1589L;
        customer.setId(customerId);
        CustomerAddress customerAddress = new CustomerAddressImpl();
        Address address = new AddressImpl();
        customerAddress.setCustomer(customer);
        customerAddress.setAddress(address);
        List<CustomerAddress> customerAddresses =
                Arrays.asList(new CustomerAddressImpl(), new CustomerAddressImpl());
        Long customerAddressId = 2L;
        customerAddress.setId(customerAddressId);
        // mock the service call that would call the database
        when(customerAddressDaoMock.readActiveCustomerAddressesByCustomerId(
                customerId
        )).thenReturn(customerAddresses);
        // mock the service call that would call the database
        when(customerAddressDaoMock.save(
                customerAddress
        )).thenReturn(customerAddress);
        CustomerAddress returnedCustomerAddress = customerAddressService.saveCustomerAddress(customerAddress);
        Assert.assertEquals(customerAddress, returnedCustomerAddress);
    }

    @Test
    public void testReadActiveCustomerAddressesByCustomerId() {
        Long customerId = 1589L;
        List<CustomerAddress> customerAddresses =
                Arrays.asList(new CustomerAddressImpl(), new CustomerAddressImpl());
        // mock the service call that would call the database
        when(customerAddressDaoMock.readActiveCustomerAddressesByCustomerId(
                customerId
        )).thenReturn(customerAddresses);
        List<CustomerAddress> returnedCustomerAddresses =
                customerAddressService.readActiveCustomerAddressesByCustomerId(customerId);
        Assert.assertEquals(customerAddresses, returnedCustomerAddresses);
    }

    @Test
    public void testReadCustomerAddressById() {
        CustomerAddress customerAddress = new CustomerAddressImpl();
        Long customerAddressId = 1L;
        // mock the service call that would call the database
        when(customerAddressDaoMock.readCustomerAddressById(customerAddressId)).thenReturn(customerAddress);
        CustomerAddress returnedCustomerAddress = customerAddressService.readCustomerAddressById(customerAddressId);
        Assert.assertEquals(customerAddress, returnedCustomerAddress);
    }

    @Test
    public void testMakeCustomerAddressDefault() {
        CustomerAddress customerAddress = new CustomerAddressImpl();
        Long customerId = 6L;
        Long customerAddressId = 1L;
        // mock the service call that would call the database
        doNothing().when(customerAddressDaoMock).makeCustomerAddressDefault(customerAddressId, customerId);
        customerAddressService.makeCustomerAddressDefault(customerAddressId, customerId);
    }

    @Test
    public void testDeleteCustomerAddressById() {
        CustomerAddress customerAddress = new CustomerAddressImpl();
        Long customerAddressId = 1L;
        // mock the service call that would call the database
        doNothing().when(customerAddressDaoMock).deleteCustomerAddressById(customerAddressId);
        customerAddressService.deleteCustomerAddressById(customerAddressId);
    }

    @Test
    public void testFindDefaultCustomerAddress() {
        CustomerAddress customerAddress = new CustomerAddressImpl();
        Long customerId = 1L;
        // mock the service call that would call the database
        when(customerAddressDaoMock.findDefaultCustomerAddress(customerId)).thenReturn(customerAddress);
        CustomerAddress returnedCustomerAddress = customerAddressService.findDefaultCustomerAddress(customerId);
        Assert.assertEquals(customerAddress, returnedCustomerAddress);
    }

    @Test
    public void testCreate() {
        CustomerAddress customerAddress = new CustomerAddressImpl();
        // mock the service call that would call the database
        when(customerAddressDaoMock.create()).thenReturn(customerAddress);
        CustomerAddress returnedCustomerAddress = customerAddressService.create();
        Assert.assertEquals(customerAddress, returnedCustomerAddress);
    }

    @Test
    public void testReadBatchAddresses() {
        List<CustomerAddress> customerAddresses =
                Arrays.asList(new CustomerAddressImpl(), new CustomerAddressImpl());
        // mock the service call that would call the database
        when(customerAddressDaoMock.readBatchCustomerAddresses(
                1,
                4
        )).thenReturn(customerAddresses);
        List<CustomerAddress> returnedCustomerAddresses = customerAddressService.readBatchAddresses(1,4);
        Assert.assertEquals(customerAddresses, returnedCustomerAddresses);
    }

    @Test
    public void testReadNumberOfAddresses() {
        Long numberOfAddresses = 2L;
        // mock the service call that would call the database
        when(customerAddressDaoMock.readNumberOfAddresses()).thenReturn(numberOfAddresses);
        Long actualNumberOfAddresses = customerAddressService.readNumberOfAddresses();
        Assert.assertEquals(numberOfAddresses, actualNumberOfAddresses);
    }
}
