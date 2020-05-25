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

import org.broadleafcommerce.profile.core.dao.CountryDao;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 *
 * @author aapowers
 *
 */
public class CountryServiceImplTest {
    @Mock
    private CountryDao countryDaoMock;

    @InjectMocks
    private CountryServiceImpl countryService;

    @Before
    public void setUp() {
        countryService = new CountryServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindCountries() {
        List<Country> countries = Arrays.asList(new CountryImpl(), new CountryImpl());
        // mock the service call that would call the database
        when(countryDaoMock.findCountries()).thenReturn(countries);
        List<Country> returnedCountries = countryService.findCountries();
        Assert.assertEquals(countries, returnedCountries);
        verify(countryDaoMock, times(1)).findCountries();
    }

    @Test
    public void testFindCountryByAbbreviation() {
        Country country = new CountryImpl();
        country.setAbbreviation("US");
        // mock the service call that would call the database
        when(countryDaoMock.findCountryByAbbreviation("US")).thenReturn(country);
        Country returnedCountry = countryService.findCountryByAbbreviation("US");
        Assert.assertEquals(country, returnedCountry);
        verify(countryDaoMock, times(1)).findCountryByAbbreviation("US");
    }

    @Test
    public void testSave() {
        Country country = new CountryImpl();
        // mock the service call that would call the database
        when(countryDaoMock.save(country)).thenReturn(country);
        Country returnedCountry = countryService.save(country);
        Assert.assertEquals(country, returnedCountry);
        verify(countryDaoMock, times(1)).save(country);
    }
}
