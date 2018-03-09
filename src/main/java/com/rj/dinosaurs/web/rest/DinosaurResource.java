package com.rj.dinosaurs.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.rj.dinosaurs.service.DinosaurService;
import com.rj.dinosaurs.web.rest.errors.BadRequestAlertException;
import com.rj.dinosaurs.web.rest.util.HeaderUtil;
import com.rj.dinosaurs.web.rest.util.PaginationUtil;
import com.rj.dinosaurs.service.dto.DinosaurDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Dinosaur.
 */
@RestController
@RequestMapping("/api")
public class DinosaurResource {

    private final Logger log = LoggerFactory.getLogger(DinosaurResource.class);

    private static final String ENTITY_NAME = "dinosaur";

    private final DinosaurService dinosaurService;

    public DinosaurResource(DinosaurService dinosaurService) {
        this.dinosaurService = dinosaurService;
    }

    /**
     * POST  /dinosaurs : Create a new dinosaur.
     *
     * @param dinosaurDTO the dinosaurDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dinosaurDTO, or with status 400 (Bad Request) if the dinosaur has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dinosaurs")
    @Timed
    public ResponseEntity<DinosaurDTO> createDinosaur(@Valid @RequestBody DinosaurDTO dinosaurDTO) throws URISyntaxException {
        log.debug("REST request to save Dinosaur : {}", dinosaurDTO);
        if (dinosaurDTO.getId() != null) {
            throw new BadRequestAlertException("A new dinosaur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DinosaurDTO result = dinosaurService.save(dinosaurDTO);
        return ResponseEntity.created(new URI("/api/dinosaurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dinosaurs : Updates an existing dinosaur.
     *
     * @param dinosaurDTO the dinosaurDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dinosaurDTO,
     * or with status 400 (Bad Request) if the dinosaurDTO is not valid,
     * or with status 500 (Internal Server Error) if the dinosaurDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dinosaurs")
    @Timed
    public ResponseEntity<DinosaurDTO> updateDinosaur(@Valid @RequestBody DinosaurDTO dinosaurDTO) throws URISyntaxException {
        log.debug("REST request to update Dinosaur : {}", dinosaurDTO);
        if (dinosaurDTO.getId() == null) {
            return createDinosaur(dinosaurDTO);
        }
        DinosaurDTO result = dinosaurService.save(dinosaurDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dinosaurDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dinosaurs : get all the dinosaurs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dinosaurs in body
     */
    @GetMapping("/dinosaurs")
    @Timed
    public ResponseEntity<List<DinosaurDTO>> getAllDinosaurs(Pageable pageable) {
        log.debug("REST request to get a page of Dinosaurs");
        Page<DinosaurDTO> page = dinosaurService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dinosaurs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dinosaurs/:id : get the "id" dinosaur.
     *
     * @param id the id of the dinosaurDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dinosaurDTO, or with status 404 (Not Found)
     */
    @GetMapping("/dinosaurs/{id}")
    @Timed
    public ResponseEntity<DinosaurDTO> getDinosaur(@PathVariable Long id) {
        log.debug("REST request to get Dinosaur : {}", id);
        DinosaurDTO dinosaurDTO = dinosaurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dinosaurDTO));
    }

    /**
     * DELETE  /dinosaurs/:id : delete the "id" dinosaur.
     *
     * @param id the id of the dinosaurDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dinosaurs/{id}")
    @Timed
    public ResponseEntity<Void> deleteDinosaur(@PathVariable Long id) {
        log.debug("REST request to delete Dinosaur : {}", id);
        dinosaurService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
