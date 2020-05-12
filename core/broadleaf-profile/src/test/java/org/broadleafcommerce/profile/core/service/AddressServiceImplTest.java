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

import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.profile.core.dao.AddressDao;
import org.broadleafcommerce.profile.core.domain.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.broadleafcommerce.profile.core.service.exception.AddressVerificationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author aapowers
 *
 */
public class AddressServiceImplTest {
    @Mock
    private CountrySubdivisionServiceImpl cssMock;

    @Mock
    private AddressDao addressDaoMock;

    @Mock
    private PhoneService phoneServiceMock;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Before
    public void setUp() {
        addressService = new AddressServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveAddress() {
        Address address = new AddressImpl();
        // mock the service call that would call the database
        when(addressDaoMock.save(address)).thenReturn(address);
        Address returnedAddress = addressService.saveAddress(address);
        Assert.assertEquals(address, returnedAddress);
    }

    @Test
    public void testReadAddressById() {
        Long addressId = 2L;
        Address address = new AddressImpl();
        address.setId(addressId);
        // mock the service call that would call the database
        when(addressDaoMock.readAddressById(addressId)).thenReturn(address);
        Address returnedAddress = addressService.readAddressById(addressId);
        Assert.assertEquals(address, returnedAddress);
    }

    @Test
    public void testCreate() {
        Address address = new AddressImpl();
        // mock the service call that would call the database
        when(addressDaoMock.create()).thenReturn(address);
        Address returnedAddress = addressService.create();
        Assert.assertEquals(address, returnedAddress);
    }

    @Test
    public void testDelete() {
        Address address = new AddressImpl();
        // mock the service call that would call the database
        doNothing().when(addressDaoMock).delete(address);
        addressService.delete(address);
    }

    @Test(expected = AddressVerificationException.class)
    public void testVerifyAddress_ShouldThrowException() throws AddressVerificationException {
        Address address = new AddressImpl();
        addressService.setMustValidateAddresses(true);
        try {
            addressService.verifyAddress(address);
        } catch (AddressVerificationException e) {
            // doing a try catch so that we can est the variable back to false so it
            // doesn't mess up other tests
            addressService.setMustValidateAddresses(false);
            throw e;
        }
    }

    @Test
    public void testVerifyAddress_WithStandardizedAddress() throws AddressVerificationException {
        Address address = new AddressImpl();
        address.setStandardized(true);
        List<Address> addresses = addressService.verifyAddress(address);
        Assert.assertTrue(addresses.contains(address));
    }

    @Test
    public void testCopyAddress_WithNullOriginal_ShouldReturnNull() {
        Address address = new AddressImpl();
        Address returnedAddress = addressService.copyAddress(address, null);
        Assert.assertNull(returnedAddress);
    }

    @Test
    public void testCopyAddress_WithNoDest_ShouldCreateDest() {
        Address addressOriginal = generateCompleteAddress();
        Address addressDest = new AddressImpl();
        // mock the service call
        when(addressDaoMock.create()).thenReturn(addressDest);
        when(phoneServiceMock.copyPhone(
                addressDest.getPhonePrimary(),
                addressOriginal.getPhonePrimary())
        ).thenReturn(addressOriginal.getPhonePrimary());
        Address returnedAddress = addressService.copyAddress(addressOriginal);
        Assert.assertEquals(addressOriginal, returnedAddress);
    }

    @Test
    public void testCopyAddress_WithDest_ShouldCopyOriginal() {
        Address addressOriginal = generateCompleteAddress();
        Address addressDest = new AddressImpl();
        // mock the service call
        when(phoneServiceMock.copyPhone(
                addressDest.getPhonePrimary(),
                addressOriginal.getPhonePrimary())
        ).thenReturn(addressOriginal.getPhonePrimary());
        Address returnedAddress = addressService.copyAddress(addressDest, addressOriginal);
        Assert.assertEquals(addressOriginal, returnedAddress);
    }

    /**
     * Tests the populateAddressISOCountrySub function with all valid input
     */
    @Test
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
            Assert.assertEquals(countrySubdivision.getAbbreviation(), address.getIsoCountrySubdivision());
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
    @Test
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
            Assert.assertNull(address.getIsoCountrySubdivision());
        }
    }

    @Test
    public void testSetMustValidateAddresses() {
        addressService.setMustValidateAddresses(true);
        Assert.assertTrue(addressService.mustValidateAddresses);
        addressService.setMustValidateAddresses(false);
        Assert.assertFalse(addressService.mustValidateAddresses);
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

    private static Address generateCompleteAddress()
    {
        Address address = new AddressImpl();
        address.setId((long) 1);
        address.setAddressLine1("1234 Happy Drive");
        address.setCity("Salem");
        address.setDefault(true);
        address.setFirstName("Potato");
        address.setLastName("Head");
        address.setFullName("Potato Head");
        address.setEmailAddress("mr.potatohead@testemail.com");
        address.setIsoCountrySubdivision("US-NY");
        address.setStateProvinceRegion("NY");
        address.setActive(true);
        address.setPostalCode("12345");
        address.setZipFour("1234");
        address.setIsoCountryAlpha2(new ISOCountryImpl());
        address.setPhonePrimary(new PhoneImpl());
        address.setMailing(true);
        address.setStreet(true);
        return address;
    }
}
