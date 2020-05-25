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

import org.broadleafcommerce.profile.core.dao.PhoneDaoImpl;
import org.broadleafcommerce.profile.core.domain.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 *
 * @author aapowers
 *
 */
public class PhoneServiceImplTest {
    @Mock
    private PhoneDaoImpl phoneDaoMock;

    @InjectMocks
    private PhoneServiceImpl phoneService;

    @Before
    public void setUp() {
        phoneService = new PhoneServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSavePhone() {
        Phone phone = new PhoneImpl();
        // mock the service call that would call the database
        when(phoneDaoMock.save(phone)).thenReturn(phone);
        Phone returnedPhone = phoneService.savePhone(phone);
        Assert.assertEquals(phone, returnedPhone);
        verify(phoneDaoMock, times(1)).save(phone);
    }

    @Test
    public void testReadPhoneById() {
        Long phoneId = 2L;
        Phone phone = new PhoneImpl();
        phone.setId(phoneId);
        // mock the service call that would call the database
        when(phoneDaoMock.readPhoneById(phoneId)).thenReturn(phone);
        Phone returnedPhone = phoneService.readPhoneById(phoneId);
        Assert.assertEquals(phone, returnedPhone);
        verify(phoneDaoMock, times(1)).readPhoneById(phoneId);
    }

    @Test
    public void testCreate() {
        Phone phone = new PhoneImpl();
        // mock the service call that would call the database
        when(phoneDaoMock.create()).thenReturn(phone);
        Phone returnedPhone = phoneService.create();
        Assert.assertEquals(phone, returnedPhone);
        verify(phoneDaoMock, times(1)).create();
    }

    @Test
    public void testCopyPhone_WithNullOriginal_ShouldReturnNull() {
        Phone phone = new PhoneImpl();
        Phone returnedPhone = phoneService.copyPhone(phone, null);
        Assert.assertNull(returnedPhone);
    }

    @Test
    public void testCopyPhone_WithNoDest_ShouldCreateDest() {
        Phone phoneOriginal = generateCompletePhone();
        Phone phoneDest = new PhoneImpl();
        // mock the service call
        when(phoneDaoMock.create()).thenReturn(phoneDest);
        Phone returnedPhone = phoneService.copyPhone(phoneOriginal);
        Assert.assertEquals(phoneOriginal, returnedPhone);
        verify(phoneDaoMock, times(1)).create();
    }

    @Test
    public void testCopyAddress_WithDest_ShouldCopyOriginal() {
        Phone phoneOriginal = generateCompletePhone();
        Phone phoneDest = new PhoneImpl();
        Phone returnedPhone = phoneService.copyPhone(phoneDest, phoneOriginal);
        Assert.assertEquals(phoneOriginal, returnedPhone);
    }

    private static Phone generateCompletePhone()
    {
        Phone phone = new PhoneImpl();
        phone.setPhoneNumber("8002320145");
        phone.setCountryCode("1");
        phone.setExtension("5");
        return phone;
    }
}
