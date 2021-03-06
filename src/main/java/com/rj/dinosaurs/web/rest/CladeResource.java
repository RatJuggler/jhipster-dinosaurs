package com.rj.dinosaurs.web.rest;

import com.rj.dinosaurs.repository.CladeRepository;
import com.rj.dinosaurs.service.CladeService;
import com.rj.dinosaurs.service.dto.CladeDTO;
import com.rj.dinosaurs.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.rj.dinosaurs.domain.Clade}.
 */
@RestController
@RequestMapping("/api")
public class CladeResource {

    private final Logger log = LoggerFactory.getLogger(CladeResource.class);

    private static final String ENTITY_NAME = "clade";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CladeService cladeService;

    private final CladeRepository cladeRepository;

    public CladeResource(CladeService cladeService, CladeRepository cladeRepository) {
        this.cladeService = cladeService;
        this.cladeRepository = cladeRepository;
    }

    /**
     * {@code POST  /clades} : Create a new clade.
     *
     * @param cladeDTO the cladeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cladeDTO, or with status {@code 400 (Bad Request)} if the clade has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clades")
    public ResponseEntity<CladeDTO> createClade(@Valid @RequestBody CladeDTO cladeDTO) throws URISyntaxException {
        log.debug("REST request to save Clade : {}", cladeDTO);
        if (cladeDTO.getId() != null) {
            throw new BadRequestAlertException("A new clade cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CladeDTO result = cladeService.save(cladeDTO);
        return ResponseEntity
            .created(new URI("/api/clades/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /clades/:id} : Updates an existing clade.
     *
     * @param id the id of the cladeDTO to save.
     * @param cladeDTO the cladeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cladeDTO,
     * or with status {@code 400 (Bad Request)} if the cladeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cladeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clades/{id}")
    public ResponseEntity<CladeDTO> updateClade(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CladeDTO cladeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Clade : {}, {}", id, cladeDTO);
        if (cladeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cladeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cladeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CladeDTO result = cladeService.save(cladeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cladeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /clades/:id} : Partial updates given fields of an existing clade, field will ignore if it is null
     *
     * @param id the id of the cladeDTO to save.
     * @param cladeDTO the cladeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cladeDTO,
     * or with status {@code 400 (Bad Request)} if the cladeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cladeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cladeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/clades/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CladeDTO> partialUpdateClade(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CladeDTO cladeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Clade partially : {}, {}", id, cladeDTO);
        if (cladeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cladeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cladeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CladeDTO> result = cladeService.partialUpdate(cladeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cladeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /clades} : get all the clades.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clades in body.
     */
    @GetMapping("/clades")
    public ResponseEntity<List<CladeDTO>> getAllClades(Pageable pageable) {
        log.debug("REST request to get a page of Clades");
        Page<CladeDTO> page = cladeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /clades/:id} : get the "id" clade.
     *
     * @param id the id of the cladeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cladeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clades/{id}")
    public ResponseEntity<CladeDTO> getClade(@PathVariable Long id) {
        log.debug("REST request to get Clade : {}", id);
        Optional<CladeDTO> cladeDTO = cladeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cladeDTO);
    }

    /**
     * {@code DELETE  /clades/:id} : delete the "id" clade.
     *
     * @param id the id of the cladeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clades/{id}")
    public ResponseEntity<Void> deleteClade(@PathVariable Long id) {
        log.debug("REST request to delete Clade : {}", id);
        cladeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
