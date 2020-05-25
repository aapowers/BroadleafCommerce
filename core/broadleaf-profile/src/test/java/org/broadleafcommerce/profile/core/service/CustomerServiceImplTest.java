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

import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.id.service.IdGenerationService;
import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.dao.CustomerForgotPasswordSecurityTokenDao;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.*;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 *
 * @author aapowers
 *
 */
public class CustomerServiceImplTest {
    @Mock
    private CustomerDao customerDaoMock;

    @Mock
    private IdGenerationService idGenerationServiceMock;

    @Mock
    private CustomerForgotPasswordSecurityTokenDao customerForgotPasswordSecurityTokenDaoMock;

    @Mock
    private PasswordEncoder passwordEncoderBeanMock;

    @Mock
    private RoleDao roleDaoMock;

    @Mock
    private EmailService emailServiceMock;

    @Mock
    private EmailInfo emailInfoMock;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Before
    public void setUp() {
        customerService = new CustomerServiceImpl();
        MockitoAnnotations.initMocks(this);

        List<PasswordUpdatedHandler> updatedHandlers = new ArrayList<>();
        updatedHandlers.add(new PasswordUpdatedHandler() {
            @Override
            public void passwordChanged(PasswordReset passwordReset, Customer customer, String newPassword) {

            }
        });
        customerService.setPasswordResetHandlers(updatedHandlers);
        customerService.setPasswordChangedHandlers(updatedHandlers);
    }

    @Test
    public void testSaveCustomer_NotRegistered_NullPassword() {
        Customer customer = new CustomerImpl();
        customer.setRegistered(false);
        // mock the service call that would call the database
        when(customerDaoMock.save(customer)).thenReturn(customer);
        Customer returnedCustomer = customerService.saveCustomer(customer);
        Assert.assertEquals(customer, returnedCustomer);
        verify(customerDaoMock, times(1)).save(customer);
    }

    @Test
    public void testSaveCustomer_Registered_WithPassword() {
        Customer customer = new CustomerImpl();
        customer.setRegistered(false);
        customer.setUnencodedPassword("asdf1234!");
        customer.setUnencodedChallengeAnswer("None");
        // mock the service call that would call the database
        when(customerDaoMock.save(customer)).thenReturn(customer);
        Customer returnedCustomer = customerService.saveCustomer(customer, true);
        Assert.assertTrue(returnedCustomer.isRegistered());
        verify(customerDaoMock, times(1)).save(customer);
    }

    @Test
    public void testSaveCustomer_AlreadyRegistered_WithPassword() {
        Customer customer = new CustomerImpl();
        customer.setRegistered(true);
        customer.setUnencodedPassword("asdf1234!");
        customer.setUnencodedChallengeAnswer("None");
        customer.setChallengeAnswer("None");
        // mock the service call that would call the database
        when(customerDaoMock.save(customer)).thenReturn(customer);
        Customer returnedCustomer = customerService.saveCustomer(customer, true);
        Assert.assertTrue(returnedCustomer.isRegistered());
        verify(customerDaoMock, times(1)).save(customer);
    }

    @Test
    public void testGenerateSecurePassword() {
        String password = customerService.generateSecurePassword();
        Assert.assertEquals(16, password.length());
    }

    @Test
    public void testRegisterCustomer_nullCustomerId() {
        Customer customer = new CustomerImpl();
        // mock the service call that would call the database
        when(customerDaoMock.save(customer)).thenReturn(customer);
        Customer returnedCustomer = customerService.registerCustomer(customer, "asdf1234!", "asdf1234!");
        Assert.assertTrue(returnedCustomer.isRegistered());
        verify(customerDaoMock, times(1)).save(customer);
    }

    @Test
    public void testRegisterCustomer_withCustomerId() {
        Customer customer = new CustomerImpl();
        long customerId = 12345L;
        customer.setId(customerId);
        // mock the service call that would call the database
        when(customerDaoMock.save(customer)).thenReturn(customer);
        Customer returnedCustomer = customerService.registerCustomer(customer, "asdf1234!", "asdf1234!");
        Assert.assertTrue(returnedCustomer.isRegistered());
        Assert.assertEquals(customer.getId(), returnedCustomer.getId());
        verify(customerDaoMock, times(1)).save(customer);
    }

    @Test
    public void testReadCustomerByEmail() {
        String email = "asdf@unittest.com";
        Customer customer = new CustomerImpl();
        customer.setEmailAddress(email);
        when(customerDaoMock.readCustomerByEmail(email)).thenReturn(customer);
        Customer returnedCustomer = customerService.readCustomerByEmail(email);
        Assert.assertEquals(email, returnedCustomer.getEmailAddress());
        verify(customerDaoMock, times(1)).readCustomerByEmail(email);
    }

    @Test
    public void testReadCustomerByUsername() {
        String username = "tester";
        Customer customer = new CustomerImpl();
        customer.setUsername(username);
        when(customerDaoMock.readCustomerByUsername(username)).thenReturn(customer);
        Customer returnedCustomer = customerService.readCustomerByUsername(username);
        Assert.assertEquals(username, customer.getUsername());
        verify(customerDaoMock, times(1)).readCustomerByUsername(username);
    }

    @Test
    public void testReadCustomerByUsername_Cacheable() {
        String username = "tester";
        Customer customer = new CustomerImpl();
        customer.setUsername(username);
        when(customerDaoMock.readCustomerByUsername(username, true)).thenReturn(customer);
        Customer returnedCustomer = customerService.readCustomerByUsername(username, true);
        Assert.assertEquals(username, customer.getUsername());
        verify(customerDaoMock, times(1)).readCustomerByUsername(username, true);
    }

    @Test
    public void testChangePassword() {
        PasswordChange passwordChange = new PasswordChange("tester");
        passwordChange.setNewPassword("asdf1234!");
        passwordChange.setPasswordChangeRequired(false);
        Customer customer = new CustomerImpl();
        when(customerDaoMock.save(customer)).thenReturn(customer);
        when(customerDaoMock.readCustomerByUsername("tester")).thenReturn(customer);
        Customer returnedCustomer = customerService.changePassword(passwordChange);
        Assert.assertEquals(passwordChange.getNewPassword(), returnedCustomer.getUnencodedPassword());
        InOrder inOrder = inOrder(customerDaoMock);
        inOrder.verify(customerDaoMock, calls(1)).readCustomerByUsername("tester");
        inOrder.verify(customerDaoMock, calls(1)).save(customer);
    }

    @Test
    public void testResetPassword() {
        PasswordReset passwordReset = new PasswordReset("tester");
        passwordReset.setPasswordChangeRequired(false);
        Customer customer = new CustomerImpl();
        when(customerDaoMock.save(customer)).thenReturn(customer);
        when(customerDaoMock.readCustomerByUsername("tester")).thenReturn(customer);
        Customer returnedCustomer = customerService.resetPassword(passwordReset);
        Assert.assertEquals(22, returnedCustomer.getUnencodedPassword().length());
        InOrder inOrder = inOrder(customerDaoMock);
        inOrder.verify(customerDaoMock, calls(1)).readCustomerByUsername("tester");
        inOrder.verify(customerDaoMock, calls(1)).save(customer);
    }

    @Test
    public void testAddAndRemovePostRegisterListener() {
        PostRegistrationObserver postRegisterListener = new PostRegistrationObserver() {
            @Override
            public void processRegistrationEvent(Customer customer) {

            }
        };
        customerService.addPostRegisterListener(postRegisterListener);
        customerService.removePostRegisterListener(postRegisterListener);
    }

    @Test
    public void testCreateCustomer_nullId() {
        Customer customer = new CustomerImpl();
        when(customerDaoMock.create()).thenReturn(customer);
        Customer returnedCustomer = customerService.createCustomer();
        Assert.assertEquals(customer, returnedCustomer);
        verify(customerDaoMock, times(1)).create();
    }

    @Test
    public void testCreateCustomerFromId_withId() {
        Long customerId = 123L;
        Customer customer = new CustomerImpl();
        when(customerDaoMock.readCustomerById(customerId)).thenReturn(null);
        when(customerDaoMock.create()).thenReturn(customer);
        Customer returnedCustomer = customerService.createCustomerFromId(customerId);
        Assert.assertEquals(customerId, returnedCustomer.getId());
        InOrder inOrder = inOrder(customerDaoMock);
        inOrder.verify(customerDaoMock, calls(1)).readCustomerById(customerId);
        inOrder.verify(customerDaoMock, calls(1)).create();
    }

    @Test
    public void testCreateCustomerFromId_withExistingCustomer() {
        Long customerId = 123L;
        Customer customer = new CustomerImpl();
        customer.setFirstName("Test");
        when(customerDaoMock.readCustomerById(customerId)).thenReturn(customer);
        Customer returnedCustomer = customerService.createCustomerFromId(customerId);
        Assert.assertEquals("Test", returnedCustomer.getFirstName());
        verify(customerDaoMock, times(1)).readCustomerById(customerId);
    }

    @Test
    public void testCreateNewCustomer_nullId() {
        Customer customer = new CustomerImpl();
        when(customerDaoMock.create()).thenReturn(customer);
        Customer returnedCustomer = customerService.createNewCustomer();
        Assert.assertEquals(customer, returnedCustomer);
        verify(customerDaoMock, times(1)).create();
    }

    @Test
    public void testDeleteCustomer() {
        Long customerId = 123L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        doNothing().when(roleDaoMock).removeCustomerRolesByCustomerId(customerId);
        doNothing().when(customerDaoMock).delete(customer);
        customerService.deleteCustomer(customer);
        InOrder inOrder = inOrder(customerDaoMock, roleDaoMock);
        inOrder.verify(roleDaoMock, calls(1)).removeCustomerRolesByCustomerId(customerId);
        inOrder.verify(customerDaoMock, calls(1)).delete(customer);
    }

    @Test
    public void testReadCustomerById() {
        Long customerId = 123L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        when(customerDaoMock.readCustomerById(customerId)).thenReturn(customer);
        Customer returnedCustomer = customerService.readCustomerById(customerId);
        Assert.assertEquals(customerId, customer.getId());
        verify(customerDaoMock, times(1)).readCustomerById(customerId);
    }

    @Test
    public void testReadCustomerByExternalId() {
        String externalId = "asdf";
        Customer customer = new CustomerImpl();
        customer.setExternalId(externalId);
        when(customerDaoMock.readCustomerByExternalId(externalId)).thenReturn(customer);
        Customer returnedCustomer = customerService.readCustomerByExternalId(externalId);
        Assert.assertEquals(externalId, customer.getExternalId());
        verify(customerDaoMock, times(1)).readCustomerByExternalId(externalId);
    }

    @Test
    public void testSetCustomerDao() {
        customerService.setCustomerDao(customerDaoMock);
    }

    @Test
    public void testIsPasswordValid_Valid() {
        when(passwordEncoderBeanMock.matches("a","a")).thenReturn(true);
        Assert.assertTrue(customerService.isPasswordValid("a", "a"));
        verify(passwordEncoderBeanMock, times(1)).matches("a","a");
    }

    @Test
    public void testIsPasswordValid_Invalid() {
        when(passwordEncoderBeanMock.matches("a","b")).thenReturn(false);
        Assert.assertFalse(customerService.isPasswordValid("a", "b"));
        verify(passwordEncoderBeanMock, times(1)).matches("a","b");
    }

    @Test
    public void testCustomerPassesCustomerRule() {
        Customer customer = new CustomerImpl();
        CustomerRuleHolder customerRuleHolder = new CustomerRuleHolder();
        boolean returned = customerService.customerPassesCustomerRule(customer, customerRuleHolder);
        Assert.assertTrue(returned);
    }

    @Test
    public void testGetPasswordResetHandlers() {
        List<PasswordUpdatedHandler> passwordUpdatedHandlers = customerService.getPasswordResetHandlers();
        Assert.assertEquals(1, passwordUpdatedHandlers.size());
    }

    @Test
    public void testInvalidateAllTokensForCustomer() {
        Long customerId = 123L;
        Customer customer = new CustomerImpl();
        customer.setId(customerId);
        List<CustomerForgotPasswordSecurityToken> tokens = new ArrayList<>();
        tokens.add(new CustomerForgotPasswordSecurityTokenImpl());
        when(customerForgotPasswordSecurityTokenDaoMock.readUnusedTokensByCustomerId(customerId)).thenReturn(tokens);
        when(customerForgotPasswordSecurityTokenDaoMock.saveToken(tokens.get(0))).thenReturn(tokens.get(0));
        customerService.invalidateAllTokensForCustomer(customer);
        InOrder inOrder = inOrder(customerForgotPasswordSecurityTokenDaoMock);
        inOrder.verify(customerForgotPasswordSecurityTokenDaoMock, calls(1)).readUnusedTokensByCustomerId(customerId);
        inOrder.verify(customerForgotPasswordSecurityTokenDaoMock, calls(1)).saveToken(tokens.get(0));
    }

    @Test
    public void testCheckCustomer_nullCustomer() {
        GenericResponse response = new GenericResponse();
        customerService.checkCustomer(null, response);
        Assert.assertTrue(response.getErrorCodesList().contains("invalidCustomer"));
    }

    @Test
    public void testCheckCustomer_noEmail() {
        Customer customer = new CustomerImpl();
        customer.setEmailAddress("");
        GenericResponse response = new GenericResponse();
        customerService.checkCustomer(customer, response);
        Assert.assertTrue(response.getErrorCodesList().contains("emailNotFound"));
    }

    @Test
    public void testCheckCustomer_deactivated() {
        Customer customer = new CustomerImpl();
        customer.setEmailAddress("asdf@unittest.com");
        customer.setDeactivated(true);
        GenericResponse response = new GenericResponse();
        customerService.checkCustomer(customer, response);
        Assert.assertTrue(response.getErrorCodesList().contains("inactiveUser"));
    }

    @Test
    public void testCheckPassword_noMatch() {
        String password = "asdf";
        String confirmation = "qwerty";
        GenericResponse response = new GenericResponse();
        customerService.checkPassword(password, confirmation, response);
        Assert.assertTrue(response.getErrorCodesList().contains("passwordMismatch"));
    }

    @Test
    public void testCheckPassword_noPassword() {
        String password = null;
        String confirmation = "qwerty";
        GenericResponse response = new GenericResponse();
        customerService.checkPassword(password, confirmation, response);
        Assert.assertTrue(response.getErrorCodesList().contains("invalidPassword"));
    }

    @Test
    public void testCheckPassword_noConfirmation() {
        String confirmation = null;
        String password = "qwerty";
        GenericResponse response = new GenericResponse();
        customerService.checkPassword(password, confirmation, response);
        Assert.assertTrue(response.getErrorCodesList().contains("invalidPassword"));
    }

    @Test
    public void testSetAndGetTokenExpiredMinutes() {
        customerService.setTokenExpiredMinutes(20);
        Assert.assertEquals(20, customerService.getTokenExpiredMinutes());
        customerService.setTokenExpiredMinutes(30);
    }

    @Test
    public void testSetAndGetPasswordTokenLength() {
        customerService.setPasswordTokenLength(20);
        Assert.assertEquals(20, customerService.getPasswordTokenLength());
        customerService.setPasswordTokenLength(22);
    }

    @Test
    public void testSetAndGetForgotPasswordEmailInfo() {
        customerService.setForgotPasswordEmailInfo(emailInfoMock);
        Assert.assertEquals(emailInfoMock, customerService.getForgotPasswordEmailInfo());
    }

    @Test
    public void testSetAndGetForgotUsernameEmailInfo() {
        customerService.setForgotUsernameEmailInfo(emailInfoMock);
        Assert.assertEquals(emailInfoMock, customerService.getForgotUsernameEmailInfo());
    }

    @Test
    public void testSetAndGetRegistrationEmailInfo() {
        customerService.setRegistrationEmailInfo(emailInfoMock);
        Assert.assertEquals(emailInfoMock, customerService.getRegistrationEmailInfo());
    }

    @Test
    public void testSetAndChangePasswordEmailInfo() {
        customerService.setChangePasswordEmailInfo(emailInfoMock);
        Assert.assertEquals(emailInfoMock, customerService.getChangePasswordEmailInfo());
    }

    @Test
    public void testReadBatchCustomers() {
        List<Customer> customers = Arrays.asList(new CustomerImpl(), new CustomerImpl());
        when(customerDaoMock.readBatchCustomers(1, 20)).thenReturn(customers);
        List<Customer> returnedCustomers = customerService.readBatchCustomers(1, 20);
        Assert.assertEquals(customers, returnedCustomers);
        verify(customerDaoMock, times(1)).readBatchCustomers(1,20);
    }

    @Test
    public void testReadNumberOfCustomers() {
        Long numberOfCustomers = 12345L;
        when(customerDaoMock.readNumberOfCustomers()).thenReturn(numberOfCustomers);
        Long returnedNumber = customerService.readNumberOfCustomers();
        Assert.assertEquals(numberOfCustomers, returnedNumber);
        verify(customerDaoMock, times(1)).readNumberOfCustomers();
    }
}
