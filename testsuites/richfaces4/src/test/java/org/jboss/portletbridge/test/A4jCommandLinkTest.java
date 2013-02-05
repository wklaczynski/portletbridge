/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.portletbridge.test;

import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.portal.api.PortalTest;
import org.jboss.arquillian.portal.api.PortalURL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.portletbridge.deployment.TestDeployment;
import org.jboss.shrinkwrap.portal.api.PortletArchive;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

//@RunWith(Arquillian.class)
@PortalTest
public class A4jCommandLinkTest {

    public static final String NEW_VALUE = "New Value";

    @Deployment
    public static PortletArchive createDeployment() {
        TestDeployment deployment = new TestDeployment(A4jCommandLinkTest.class, true);
        deployment.archive()
                .createFacesPortlet("A4jCommandLink", "A4J Command Link Portlet", "a4jLink.xhtml")
                .addAsWebResource("a4jLink.xhtml", "a4jLink.xhtml")
                .addClass(Bean.class);

        deployment.addMavenDependency("org.richfaces.core:richfaces-core-impl",
                "org.richfaces.core:richfaces-components-api",
                "org.richfaces.core:richfaces-components-ui");
        return deployment.getFinalArchive();
    }

    protected static final By OUTPUT_FIELD = By.id("output");
    protected static final By INPUT_FIELD = By.xpath("//input[@type='text']");
    protected static final By SUBMIT_BUTTON = By.xpath("//a");

    @ArquillianResource
    @PortalURL
    URL portalURL;

    @Drone
    WebDriver browser;

    @Before
    public void getNewSession() {
        browser.manage().deleteAllCookies();
    }

    //@Test
    @RunAsClient
    public void renderFormPortlet() throws Exception {
        browser.get(portalURL.toString());

        assertTrue("output text should contain: " + Bean.HELLO_JSF_PORTLET,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, Bean.HELLO_JSF_PORTLET).apply(browser));

        assertTrue("input text should contain: " + Bean.HELLO_JSF_PORTLET,
                ExpectedConditions.textToBePresentInElementValue(INPUT_FIELD, Bean.HELLO_JSF_PORTLET).apply(browser));

        assertTrue("Submit button text should be 'Ok'", browser.findElement(SUBMIT_BUTTON).getText().equals("Ok"));
    }

    // @Test
    public void testSubmitAndRemainOnPage() throws Exception {
        browser.get(portalURL.toString());
        browser.findElement(INPUT_FIELD).sendKeys(NEW_VALUE);
        browser.findElement(SUBMIT_BUTTON).click();

        assertTrue("output text should contain: " + NEW_VALUE,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, NEW_VALUE).apply(browser));

        assertTrue("input text should contain: " + NEW_VALUE,
                ExpectedConditions.textToBePresentInElementValue(INPUT_FIELD, NEW_VALUE).apply(browser));

        // Re-render page
        browser.get(portalURL.toString());
        assertTrue("output text should contain: " + NEW_VALUE,
                ExpectedConditions.textToBePresentInElement(OUTPUT_FIELD, NEW_VALUE).apply(browser));

        assertTrue("input text should contain: " + NEW_VALUE,
                ExpectedConditions.textToBePresentInElementValue(INPUT_FIELD, NEW_VALUE).apply(browser));
    }

}
