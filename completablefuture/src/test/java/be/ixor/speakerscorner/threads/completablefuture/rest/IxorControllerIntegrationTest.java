package be.ixor.speakerscorner.threads.completablefuture.rest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IxorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void normal() throws Exception {
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .get("/ixor/normal"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).isEqualTo("one");
    }

    @Test
    void completable() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/ixor/completable"))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        String one = mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andExpect(content().string("one"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(one).isEqualTo("one");
    }

    @Test
    void serial() throws Exception {
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .get("/ixor/serial"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).isEqualTo("one two");
    }

    @Test
    void parallel() throws Exception {
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .get("/ixor/parallel"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).isEqualTo("one two");
    }

    @Test
    void virtual1() throws Exception {
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .get("/ixor/virtual1"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).contains("one two");
    }

    @Disabled
    @Test
    void virtual2() throws Exception {
        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .get("/ixor/virtual2"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(contentAsString).contains("quotation");
    }

}