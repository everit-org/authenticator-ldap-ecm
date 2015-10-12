/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.authenticator.ldap.ecm.tests;

import java.util.Optional;

import org.everit.authenticator.Authenticator;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.junit.Assert;
import org.junit.Test;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Integration test component of the LdapAuthenticatorComponent.
 */
@Component(componentId = "LdapAuthenticatorTest",
    configurationPolicy = ConfigurationPolicy.OPTIONAL)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE,
        defaultValue = "junit4"),
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID,
        defaultValue = "LdapAuthenticatorTest") })
@Service(value = LdapAuthenticatorTestComponent.class)
public class LdapAuthenticatorTestComponent {

  private Authenticator authenticator;

  @ServiceRef(defaultValue = "")
  public void setAuthenticator(final Authenticator authenticator) {
    this.authenticator = authenticator;
  }

  @Test
  public void testAuthenticate() throws Exception {
    String principal = LdapTestConstants.FOO_MAIL;
    String mappedPrincipal = LdapTestConstants.CN_FOO;
    String credential = LdapTestConstants.FOO_CREDENTIAL;

    Optional<String> optionalMappedPrincipal = authenticator.authenticate(principal, credential);
    Assert.assertNotNull(optionalMappedPrincipal);
    Assert.assertTrue(optionalMappedPrincipal.isPresent());
    Assert.assertEquals(mappedPrincipal, optionalMappedPrincipal.get());

    optionalMappedPrincipal = authenticator.authenticate(principal, principal);
    Assert.assertNotNull(optionalMappedPrincipal);
    Assert.assertFalse(optionalMappedPrincipal.isPresent());

    optionalMappedPrincipal = authenticator.authenticate(credential, credential);
    Assert.assertNotNull(optionalMappedPrincipal);
    Assert.assertFalse(optionalMappedPrincipal.isPresent());
  }

  @Test
  public void testValidations() {
    try {
      authenticator.authenticate(null, null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("principal cannot be null"));
    }
    try {
      authenticator.authenticate(" ", null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("principal cannot be empty/blank"));
    }
    try {
      authenticator.authenticate("a", null);
      Assert.fail();
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("credential cannot be null"));
    }
    try {
      authenticator.authenticate("a", " ");
      Assert.fail();
    } catch (IllegalArgumentException e) {
      Assert.assertTrue(e.getMessage().contains("credential cannot be empty/blank"));
    }

    authenticator.authenticate("a ", "a ");

  }

}
