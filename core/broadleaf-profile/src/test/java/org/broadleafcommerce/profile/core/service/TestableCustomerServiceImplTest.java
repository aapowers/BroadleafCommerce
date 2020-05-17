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
import org.broadleafcommerce.common.security.util.PasswordUtils;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.dao.CustomerForgotPasswordSecurityTokenDao;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityToken;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityTokenImpl;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 *
 * @author aapowers
 *
 */
public class TestableCustomerServiceImplTest {
    @Mock
    private PasswordUtils passwordUtilsMock;

    @Mock
    private CustomerDao customerDaoMock;

    @Mock
    private EmailService emailServiceMock;

    @Mock
    private PasswordEncoder passwordEncoderBeanMock;

    @Mock
    private CustomerForgotPasswordSecurityTokenDao customerForgotPasswordSecurityTokenDaoMock;

    @InjectMocks
    private TestableCustomerServiceImpl customerService;

    @Before
    public void setUp() {
        customerService = new TestableCustomerServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendForgotPasswordNotification() {
        Customer customer = new CustomerImpl();
        customer.setId(1L);
        customer.setEmailAddress("asdf@unittest.com");
        EmailInfo emailInfo = new EmailInfo();
        HashMap<String, Object> vars = new HashMap<String, Object>();
        vars.put("token", "hello");
        vars.put("resetPasswordUrl", "/asdf&token=hello");
        customerService.setForgotPasswordEmailInfo(emailInfo);
        when(passwordUtilsMock.testableGenerateSecurePassword(anyInt())).thenReturn("HELLO");
        when(customerDaoMock.readCustomerByUsername("asdf")).thenReturn(customer);
        when(emailServiceMock.sendTemplateEmail("asdf@unittest.com", emailInfo, vars)).thenReturn(true);
        when(passwordEncoderBeanMock.encode("hello")).thenReturn("hello");
        when(customerForgotPasswordSecurityTokenDaoMock.saveToken(any())).thenReturn(new CustomerForgotPasswordSecurityTokenImpl());

        GenericResponse genericResponse = customerService.sendForgotPasswordNotification("asdf", "/asdf");
        Assert.assertFalse(genericResponse.getHasErrors());
        InOrder inOrder = inOrder(passwordUtilsMock, passwordEncoderBeanMock, customerForgotPasswordSecurityTokenDaoMock, emailServiceMock);
        inOrder.verify(passwordUtilsMock, times(1)).testableGenerateSecurePassword(anyInt());
        inOrder.verify(passwordEncoderBeanMock, times(1)).encode("hello");
        inOrder.verify(customerForgotPasswordSecurityTokenDaoMock, times(1)).saveToken(any());
        inOrder.verify(emailServiceMock, times(1)).sendTemplateEmail(anyString(), any(), anyMap());
    }
}
