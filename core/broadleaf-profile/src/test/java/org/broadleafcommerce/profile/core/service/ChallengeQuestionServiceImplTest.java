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

import org.broadleafcommerce.profile.core.dao.ChallengeQuestionDao;
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
public class ChallengeQuestionServiceImplTest {
    @Mock
    private ChallengeQuestionDao challengeQuestionDaoMock;

    @InjectMocks
    private ChallengeQuestionServiceImpl challengeQuestionService;

    @Before
    public void setUp() {
        challengeQuestionService = new ChallengeQuestionServiceImpl();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReadChallengeQuestions() {
        List<ChallengeQuestion> challengeQuestions =
                Arrays.asList(new ChallengeQuestionImpl(), new ChallengeQuestionImpl());
        // mock the service call that would call the database
        when(challengeQuestionDaoMock.readChallengeQuestions()).thenReturn(challengeQuestions);
        List<ChallengeQuestion> returnedChallengeQuestions = challengeQuestionService.readChallengeQuestions();
        Assert.assertEquals(challengeQuestions, returnedChallengeQuestions);
    }

    @Test
    public void testReadChallengeQuestionById() {
        ChallengeQuestion challengeQuestion = new ChallengeQuestionImpl();
        long challengeQuestionId = 1L;
        // mock the service call that would call the database
        when(challengeQuestionDaoMock.readChallengeQuestionById(challengeQuestionId)).thenReturn(challengeQuestion);
        ChallengeQuestion returnedChallengeQuestion = challengeQuestionService.readChallengeQuestionById(challengeQuestionId);
        Assert.assertEquals(challengeQuestion, returnedChallengeQuestion);
    }
}
