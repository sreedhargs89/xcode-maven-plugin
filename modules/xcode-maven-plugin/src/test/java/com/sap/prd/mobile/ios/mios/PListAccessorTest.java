/*
 * #%L
 * xcode-maven-plugin
 * %%
 * Copyright (C) 2012 SAP AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sap.prd.mobile.ios.mios;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PListAccessorTest extends XCodeTest
{
  private static final String BUNDLE_IDENTIFIER_KEY = "CFBundleIdentifier";

  private PListAccessor plist;

  private File tempFile;

  @Before
  public void before() throws IOException
  {
    File srcFile = new File("src/test/resources/MyApp-Info.plist");
    tempFile = createTempFile();
    org.apache.commons.io.FileUtils.copyFile(srcFile, tempFile);
    loadPList();
  }

  private File createTempFile() throws IOException
  {
    File temp = File.createTempFile("Info", ".plist");
    temp.deleteOnExit();
    return temp;
  }

  @After
  public void after()
  {
    tempFile.delete();
  }

  private void loadPList()
  {
    plist = new PListAccessor(tempFile);
  }

  @Test
  public void readString() throws IOException
  {
    String appId = plist.getStringValue(BUNDLE_IDENTIFIER_KEY);
    assertEquals("com.sap.myapp", appId);
  }

  @Test
  public void writeString() throws IOException
  {
    testWrite("com.sap.myapp.internal");
  }

  @Test
  public void writeStringWithWhiteSpace() throws IOException
  {
    testWrite("com.sap.myapp.internal with space");
  }

  private void testWrite(String expectedAppId) throws IOException
  {
    plist.setStringValue(BUNDLE_IDENTIFIER_KEY, expectedAppId);
    String appId = plist.getStringValue(BUNDLE_IDENTIFIER_KEY);
    assertEquals(expectedAppId, appId);

    loadPList();
    appId = plist.getStringValue(BUNDLE_IDENTIFIER_KEY);
    assertEquals(expectedAppId, appId);
  }

  @Test(expected = FileNotFoundException.class)
  public void readStringFromNonExistingPList() throws IOException
  {
    PListAccessor plist = new PListAccessor(new File("/foo"));
    plist.getStringValue(BUNDLE_IDENTIFIER_KEY);
  }

  @Test(expected = FileNotFoundException.class)
  public void writeStringToNonExistingPList() throws IOException
  {
    PListAccessor plist = new PListAccessor(new File("/foo"));
    plist.setStringValue(BUNDLE_IDENTIFIER_KEY, "foo");
  }

  @Test(expected = IllegalStateException.class)
  public void readStringFromNonExistingKey() throws IOException
  {
    plist.getStringValue("foo");
  }

  @Test
  public void writeStringToNonExistingKey() throws IOException
  {
    plist.setStringValue("foo", "com.sap.myapp.internal");
    String appId = plist.getStringValue("foo");
    assertEquals("com.sap.myapp.internal", appId);

    loadPList();
    appId = plist.getStringValue("foo");
    assertEquals("com.sap.myapp.internal", appId);
  }

}
