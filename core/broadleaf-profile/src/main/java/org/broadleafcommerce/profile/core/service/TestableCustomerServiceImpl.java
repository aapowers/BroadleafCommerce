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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.id.service.IdGenerationService;
import org.broadleafcommerce.common.rule.MvelHelper;
import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.common.security.util.PasswordUtils;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.dao.CustomerForgotPasswordSecurityTokenDao;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.*;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * This is an extension of CustomerServiceImpl. I am extending it so that I can add PasswordUtils
 * via dependency injection, so the password generation can be mocked in a unit test.
 */
@Service("blCustomerService")
public class TestableCustomerServiceImpl extends CustomerServiceImpl {

    @Resource(name = "passwordUtils")
    protected PasswordUtils passwordUtils;

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public GenericResponse sendForgotPasswordNotification(String username, String resetPasswordUrl) {
        GenericResponse response = new GenericResponse();
        Customer customer = null;

        if (username != null) {
            customer = customerDao.readCustomerByUsername(username);
        }

        checkCustomer(customer, response);

        if (!response.getHasErrors()) {
            String token = passwordUtils.testableGenerateSecurePassword(getPasswordTokenLength());
            token = token.toLowerCase();

            CustomerForgotPasswordSecurityToken fpst = new CustomerForgotPasswordSecurityTokenImpl();
            fpst.setCustomerId(customer.getId());
            fpst.setToken(encodePassword(token));
            fpst.setCreateDate(SystemTime.asDate());
            customerForgotPasswordSecurityTokenDao.saveToken(fpst);

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("token", token);
            if (!StringUtils.isEmpty(resetPasswordUrl)) {
                if (resetPasswordUrl.contains("?")) {
                    resetPasswordUrl = resetPasswordUrl + "&token=" + token;
                } else {
                    resetPasswordUrl = resetPasswordUrl + "?token=" + token;
                }
            }
            vars.put("resetPasswordUrl", resetPasswordUrl);
            sendEmail(customer.getEmailAddress(), getForgotPasswordEmailInfo(), vars);
        }
        return response;
    }
}
