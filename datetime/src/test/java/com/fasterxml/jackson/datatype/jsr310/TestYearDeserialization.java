package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;

import java.io.IOException;
import java.time.Year;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestYearDeserialization extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(Year.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(Year.of(2000), "'2000'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notayear'");
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
    	try {
    		read("['2000']");
    	    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    }
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
    	try {
    		read("[]");
    	    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    	try {
    		String json="[]";
        	newMapper()
        			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
        			.readerFor(Year.class).readValue(aposToQuotes(json));
    	    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
    	String json="['2000']";
    	Year value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.readerFor(Year.class).readValue(aposToQuotes(json));
    	notNull(value);
        expect(Year.of(2000), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
    	String json="[]";
    	Year value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    			.readerFor(Year.class).readValue(aposToQuotes(json));
    	assertNull(value);
    }
    
    private void expectFailure(String json) throws Throwable {
        try {
            read(json);
            fail("expected DateTimeParseException");
        } catch (JsonProcessingException e) {
            if (e.getCause() == null) {
                throw e;
            }
            if (!(e.getCause() instanceof DateTimeParseException)) {
                throw e.getCause();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void expectSuccess(Object exp, String json) throws IOException {
        final Year value = read(json);
        notNull(value);
        expect(exp, value);
    }

    private Year read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
