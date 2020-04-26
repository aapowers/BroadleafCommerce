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

import junit.framework.TestCase;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.profile.core.domain.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author aapowers
 *
 */
public class AddressServiceImplTest extends TestCase {
    @Mock
    private CountrySubdivisionServiceImpl cssMock;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Override
    protected void setUp() {
        addressService = new AddressServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests the populateAddressISOCountrySub function with all valid input
     */
    public void testPopulateAddressISOCountrySub_WithValidInput_ShouldPopulateISOCountrySub() {
        List<Object[]> validAddresses = getValidAddresses();
        for (Object[] validAddress : validAddresses ) {
            Address address = (Address) validAddress[0];
            CountrySubdivision countrySubdivision = (CountrySubdivision) validAddress[1];

            // mock the service call that would call the database
            when(cssMock.findSubdivisionByCountryAndAltAbbreviation(
                    address.getIsoCountryAlpha2().getAlpha2(),
                    address.getStateProvinceRegion()
            )).thenReturn(countrySubdivision);

            // run the test and assert the correct ISO code
            addressService.populateAddressISOCountrySub(address);
            assertEquals(countrySubdivision.getAbbreviation(),address.getIsoCountrySubdivision());
        }
    }

    private static List<Object[]> getValidAddresses() {
        return Arrays.asList(
                getValidAddress("US", "CA", "US-CA"),
                getValidAddress("CA", "QA", "CA-QC"),
                getValidAddress("FR", "NOR", "FR-NOR"),
                getValidAddress("KR", "11", "KR-11")
        );
    }

    private static Object[] getValidAddress(String country, String state, String ISOCode)
    {
        // set up the ISO country
        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2(country);

        // set up the address
        Address address;
        address = new AddressImpl();
        address.setIsoCountryAlpha2(isoCountry);
        address.setStateProvinceRegion(state);

        // set up the subdivision
        CountrySubdivision countrySubdivision;
        countrySubdivision = new CountrySubdivisionImpl();
        countrySubdivision.setAbbreviation(ISOCode);

        return new Object[]{address, countrySubdivision};
    }

    /**
     * Tests the populateAddressISOCountrySub function with all invalid input
     */
    public void testPopulateAddressISOCountrySub_WithInValidInput_ShouldNotPopulateISOCountrySub() {
        List<Object[]> invalidAddresses = getInvalidAddresses();
        for (Object[] invalidAddress : invalidAddresses ) {
            Address address = (Address) invalidAddress[0];
            CountrySubdivision countrySubdivision = (CountrySubdivision) invalidAddress[1];

            // mock the service call that would call the database
            when(cssMock.findSubdivisionByCountryAndAltAbbreviation(
                    address.getIsoCountryAlpha2().getAlpha2(),
                    address.getStateProvinceRegion()
            )).thenReturn(countrySubdivision);

            // run the test and assert that there is no ISO code
            addressService.populateAddressISOCountrySub(address);
            assertNull(address.getIsoCountrySubdivision());
        }
    }

    private static List<Object[]> getInvalidAddresses() {
        return Arrays.asList(
                getInvalidAddress("", "CA"),
                getInvalidAddress("US", ""),
                getInvalidAddress("Codeland", "CA"),
                getInvalidAddress("US", "999"),
                getInvalidAddress("FR", "CA")
        );
    }

    private static Object[] getInvalidAddress(String country, String state)
    {
        // set up the ISO country
        ISOCountry isoCountry = new ISOCountryImpl();
        isoCountry.setAlpha2(country);

        // set up the address
        Address address;
        address = new AddressImpl();
        address.setIsoCountryAlpha2(isoCountry);
        address.setStateProvinceRegion(state);

        return new Object[]{address, null};
    }
}
