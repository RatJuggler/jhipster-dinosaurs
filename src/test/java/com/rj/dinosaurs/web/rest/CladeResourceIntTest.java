package com.rj.dinosaurs.web.rest;

import com.rj.dinosaurs.DinosaursApp;

import com.rj.dinosaurs.domain.Clade;
import com.rj.dinosaurs.repository.CladeRepository;
import com.rj.dinosaurs.service.CladeService;
import com.rj.dinosaurs.service.dto.CladeDTO;
import com.rj.dinosaurs.service.mapper.CladeMapper;
import com.rj.dinosaurs.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.rj.dinosaurs.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CladeResource REST controller.
 *
 * @see CladeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DinosaursApp.class)
public class CladeResourceIntTest {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private CladeRepository cladeRepository;

    @Autowired
    private CladeMapper cladeMapper;

    @Autowired
    private CladeService cladeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCladeMockMvc;

    private Clade clade;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CladeResource cladeResource = new CladeResource(cladeService);
        this.restCladeMockMvc = MockMvcBuilders.standaloneSetup(cladeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clade createEntity(EntityManager em) {
        Clade clade = new Clade()
            .description(DEFAULT_DESCRIPTION);
        return clade;
    }

    @Before
    public void initTest() {
        clade = createEntity(em);
    }

    @Test
    @Transactional
    public void createClade() throws Exception {
        int databaseSizeBeforeCreate = cladeRepository.findAll().size();

        // Create the Clade
        CladeDTO cladeDTO = cladeMapper.toDto(clade);
        restCladeMockMvc.perform(post("/api/clades")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cladeDTO)))
            .andExpect(status().isCreated());

        // Validate the Clade in the database
        List<Clade> cladeList = cladeRepository.findAll();
        assertThat(cladeList).hasSize(databaseSizeBeforeCreate + 1);
        Clade testClade = cladeList.get(cladeList.size() - 1);
        assertThat(testClade.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createCladeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = cladeRepository.findAll().size();

        // Create the Clade with an existing ID
        clade.setId(1L);
        CladeDTO cladeDTO = cladeMapper.toDto(clade);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCladeMockMvc.perform(post("/api/clades")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cladeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Clade in the database
        List<Clade> cladeList = cladeRepository.findAll();
        assertThat(cladeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = cladeRepository.findAll().size();
        // set the field null
        clade.setDescription(null);

        // Create the Clade, which fails.
        CladeDTO cladeDTO = cladeMapper.toDto(clade);

        restCladeMockMvc.perform(post("/api/clades")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cladeDTO)))
            .andExpect(status().isBadRequest());

        List<Clade> cladeList = cladeRepository.findAll();
        assertThat(cladeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClades() throws Exception {
        // Initialize the database
        cladeRepository.saveAndFlush(clade);

        // Get all the cladeList
        restCladeMockMvc.perform(get("/api/clades?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clade.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getClade() throws Exception {
        // Initialize the database
        cladeRepository.saveAndFlush(clade);

        // Get the clade
        restCladeMockMvc.perform(get("/api/clades/{id}", clade.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(clade.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClade() throws Exception {
        // Get the clade
        restCladeMockMvc.perform(get("/api/clades/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClade() throws Exception {
        // Initialize the database
        cladeRepository.saveAndFlush(clade);
        int databaseSizeBeforeUpdate = cladeRepository.findAll().size();

        // Update the clade
        Clade updatedClade = cladeRepository.findOne(clade.getId());
        // Disconnect from session so that the updates on updatedClade are not directly saved in db
        em.detach(updatedClade);
        updatedClade
            .description(UPDATED_DESCRIPTION);
        CladeDTO cladeDTO = cladeMapper.toDto(updatedClade);

        restCladeMockMvc.perform(put("/api/clades")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cladeDTO)))
            .andExpect(status().isOk());

        // Validate the Clade in the database
        List<Clade> cladeList = cladeRepository.findAll();
        assertThat(cladeList).hasSize(databaseSizeBeforeUpdate);
        Clade testClade = cladeList.get(cladeList.size() - 1);
        assertThat(testClade.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingClade() throws Exception {
        int databaseSizeBeforeUpdate = cladeRepository.findAll().size();

        // Create the Clade
        CladeDTO cladeDTO = cladeMapper.toDto(clade);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCladeMockMvc.perform(put("/api/clades")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(cladeDTO)))
            .andExpect(status().isCreated());

        // Validate the Clade in the database
        List<Clade> cladeList = cladeRepository.findAll();
        assertThat(cladeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteClade() throws Exception {
        // Initialize the database
        cladeRepository.saveAndFlush(clade);
        int databaseSizeBeforeDelete = cladeRepository.findAll().size();

        // Get the clade
        restCladeMockMvc.perform(delete("/api/clades/{id}", clade.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Clade> cladeList = cladeRepository.findAll();
        assertThat(cladeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Clade.class);
        Clade clade1 = new Clade();
        clade1.setId(1L);
        Clade clade2 = new Clade();
        clade2.setId(clade1.getId());
        assertThat(clade1).isEqualTo(clade2);
        clade2.setId(2L);
        assertThat(clade1).isNotEqualTo(clade2);
        clade1.setId(null);
        assertThat(clade1).isNotEqualTo(clade2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CladeDTO.class);
        CladeDTO cladeDTO1 = new CladeDTO();
        cladeDTO1.setId(1L);
        CladeDTO cladeDTO2 = new CladeDTO();
        assertThat(cladeDTO1).isNotEqualTo(cladeDTO2);
        cladeDTO2.setId(cladeDTO1.getId());
        assertThat(cladeDTO1).isEqualTo(cladeDTO2);
        cladeDTO2.setId(2L);
        assertThat(cladeDTO1).isNotEqualTo(cladeDTO2);
        cladeDTO1.setId(null);
        assertThat(cladeDTO1).isNotEqualTo(cladeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(cladeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(cladeMapper.fromId(null)).isNull();
    }
}
