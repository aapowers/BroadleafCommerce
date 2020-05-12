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

import org.broadleafcommerce.profile.core.dao.CountrySubdivisionDao;
import org.broadleafcommerce.profile.core.domain.*;
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
public class CountrySubdivisionServiceImplTest {
    @Mock
    private CountrySubdivisionDao countrySubdivisionDaoMock;

    @InjectMocks
    private CountrySubdivisionService countrySubdivisionService;

    @Before
    public void setUp() {
        countrySubdivisionService = new CountrySubdivisionServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindSubdivisions_WithoutAbbreviation() {
        List<CountrySubdivision> countrySubdivisions = Arrays.asList(new CountrySubdivisionImpl(), new CountrySubdivisionImpl());
        // mock the service call that would call the database
        when(countrySubdivisionDaoMock.findSubdivisions()).thenReturn(countrySubdivisions);
        List<CountrySubdivision> returnedCountrySubdivisions = countrySubdivisionService.findSubdivisions();
        Assert.assertEquals(countrySubdivisions, returnedCountrySubdivisions);
    }

    @Test
    public void testFindSubdivisions_WithAbbreviation() {
        List<CountrySubdivision> countrySubdivisions = Arrays.asList(new CountrySubdivisionImpl(), new CountrySubdivisionImpl());
        // mock the service call that would call the database
        when(countrySubdivisionDaoMock.findSubdivisions("US")).thenReturn(countrySubdivisions);
        List<CountrySubdivision> returnedCountrySubdivisions = countrySubdivisionService.findSubdivisions("US");
        Assert.assertEquals(countrySubdivisions, returnedCountrySubdivisions);
    }

    @Test
    public void testFindSubdivisionsByCountryAndCategory() {
        List<CountrySubdivision> countrySubdivisions = Arrays.asList(new CountrySubdivisionImpl(), new CountrySubdivisionImpl());
        // mock the service call that would call the database
        when(countrySubdivisionDaoMock.findSubdivisionsByCountryAndCategory(
                "US",
                "Territories"
        )).thenReturn(countrySubdivisions);
        List<CountrySubdivision> returnedCountrySubdivisions =
                countrySubdivisionService.findSubdivisionsByCountryAndCategory(
                        "US",
                        "Territories"
                );
        Assert.assertEquals(countrySubdivisions, returnedCountrySubdivisions);
    }

    @Test
    public void testFindSubdivisionByAbbreviation() {
        CountrySubdivision countrySubdivision = new CountrySubdivisionImpl();
        // mock the service call that would call the database
        when(countrySubdivisionDaoMock.findSubdivisionByAbbreviation("CA")).thenReturn(countrySubdivision);
        CountrySubdivision returnedCountrySubdivision =
                countrySubdivisionService.findSubdivisionByAbbreviation("CA");
        Assert.assertEquals(countrySubdivision, returnedCountrySubdivision);
    }

    @Test
    public void testFindSubdivisionByCountryAndAltAbbreviation() {
        CountrySubdivision countrySubdivision = new CountrySubdivisionImpl();
        // mock the service call that would call the database
        when(countrySubdivisionDaoMock.findSubdivisionByCountryAndAltAbbreviation(
                "US",
                "CALI"
        )).thenReturn(countrySubdivision);
        CountrySubdivision returnedCountrySubdivision =
                countrySubdivisionService.findSubdivisionByCountryAndAltAbbreviation("US","CALI");
        Assert.assertEquals(countrySubdivision, returnedCountrySubdivision);
    }

    @Test
    public void testFindSubdivisionByCountryAndName() {
        CountrySubdivision countrySubdivision = new CountrySubdivisionImpl();
        // mock the service call that would call the database
        when(countrySubdivisionDaoMock.findSubdivisionByCountryAndName(
                "US",
                "California"
        )).thenReturn(countrySubdivision);
        CountrySubdivision returnedCountrySubdivision =
                countrySubdivisionService.findSubdivisionByCountryAndName("US","California");
        Assert.assertEquals(countrySubdivision, returnedCountrySubdivision);
    }

    @Test
    public void testSave() {
        CountrySubdivision countrySubdivision = new CountrySubdivisionImpl();
        // mock the service call that would call the database
        when(countrySubdivisionDaoMock.save(countrySubdivision)).thenReturn(countrySubdivision);
        CountrySubdivision returnedCountrySubdivision = countrySubdivisionService.save(countrySubdivision);
        Assert.assertEquals(countrySubdivision, returnedCountrySubdivision);
    }
}
