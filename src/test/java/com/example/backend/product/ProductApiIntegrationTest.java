package com.example.backend.product;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {
        "app.seed-data=false",
        "spring.datasource.url=jdbc:h2:mem:producttest;DB_CLOSE_DELAY=-1"
})
class ProductApiIntegrationTest {

    private static final String SAMPLE_BODY = """
            {"name":"Keyboard","description":"Mechanical keyboard","price":99.99,"stock":10}
            """;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        repository.deleteAll();
    }

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SAMPLE_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/products/")))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getReturnsProduct() throws Exception {
        int id = createProduct();

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    void getMissingReturns404ProblemDetail() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"));
    }

    @Test
    void listReturnsPagedContent() throws Exception {
        createProduct();
        createProduct();

        mockMvc.perform(get("/api/v1/products").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void updateChangesFields() throws Exception {
        int id = createProduct();
        String updated = """
                {"name":"Keyboard v2","description":"Updated","price":109.00,"stock":5}
                """;

        mockMvc.perform(put("/api/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Keyboard v2"))
                .andExpect(jsonPath("$.price").value(109.00))
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    void deleteRemovesProduct() throws Exception {
        int id = createProduct();

        mockMvc.perform(delete("/api/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/products/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithInvalidBodyReturns400() throws Exception {
        String invalid = """
                {"name": "", "price": -1, "stock": -5}
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.stock").exists());
    }

    private int createProduct() throws Exception {
        String response = mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SAMPLE_BODY))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.parse(response).read("$.id", Integer.class);
    }
}
