package viarzilin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.yml")
@Sql(value = {"/create-user-before.sql", "/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/messages-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void greetingPageTest() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Hello, guest")));
    }

    @WithUserDetails("admin")
    @Test
    void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='navbarSupportedContent']/div").string("admin"));
    }

    @WithUserDetails("admin")
    @Test
    void messageListTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(4));
    }

    @WithUserDetails("admin")
    @Test
    void filterMessageTest() throws Exception {
        this.mockMvc.perform(get("/main").param("filter", "my-tag"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(2))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='1']").exists())
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='3']").exists());
    }

    @WithUserDetails("admin")
    @Test
    void addMessageToListTest() throws Exception {
        this.mockMvc.perform(multipart("/main")
                .file("file", "123".getBytes())
                .param("text", "fifth")
                .param("tag", "new tag")
                .with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(5))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']").exists())
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/span").string("fifth"))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/i").string("#new tag"));
    }

    @WithUserDetails("admin")
    @Test
    void addMessageWithoutFileTest() throws Exception {
        this.mockMvc.perform(multipart("/main")
                .param("text", "sixth")
                .param("tag", "no-file")
                .with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(5))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']").exists())
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/span").string("sixth"))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/i").string("#no-file"));
    }

    @WithUserDetails("admin")
    @Test
    void addMessageValidationErrorTest() throws Exception {
        this.mockMvc.perform(multipart("/main")
                .param("text", "")
                .param("tag", "test")
                .with(csrf()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(4))
                .andExpect(model().attributeHasFieldErrors("message", "text"));
    }

    @WithUserDetails("admin")
    @Test
    void likeMessageTest() throws Exception {
        this.mockMvc.perform(get("/messages/1/like"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"));
    }

    @WithUserDetails("admin")
    @Test
    void likeMessageWithRefererTest() throws Exception {
        this.mockMvc.perform(get("/messages/1/like")
                        .header("referer", "/main?page=2&filter=test"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/main?page=2&filter=test"));
    }

    @WithUserDetails("admin")
    @Test
    void unlikeMessageTest() throws Exception {
        this.mockMvc.perform(get("/messages/1/like"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

        this.mockMvc.perform(get("/messages/1/like"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}