package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * This is a REST controller for UCSBOrganization
 */

@Tag(name = "UCSBOrganization")
@RequestMapping("/api/ucsborganization")
@RestController
@Slf4j
public class UCSBOrganizationController extends ApiController {

    @Autowired
    UCSBOrganizationRepository ucsbOrganizationRepository;

    /**
     * THis method returns a list of all ucsborganizations.
     * @return a list of all ucsborganizations
     */
    @Operation(summary= "List all organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganization> allOrganizations() {
        Iterable<UCSBOrganization> commons = ucsbOrganizationRepository.findAll();
        return commons;
    }

    /**
     * This method returns a single organization.
     * @param code code of the diningcommons
     * @return a single diningcommons
     */
    @Operation(summary= "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganization getById(
            @Parameter(name="code") @RequestParam String code) {
        UCSBOrganization commons = ucsbOrganizationRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, code));

        return commons;
    }

    /**
     * This method creates a new organization. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode code of the organization
     * @param orgTranslationShort shortened translation of the organization
     * @param orgTranslation shortened translation of the organization
     * @param inactive whether or not the organization is inactive
     * @return the saved organization
     */
    @Operation(summary= "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganization postCommons(
        @Parameter(name="orgCode") @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
        @Parameter(name="inactive") @RequestParam boolean inactive
        )
        {

        UCSBOrganization organization = new UCSBOrganization();
        organization.setOrgCode(orgCode);
        organization.setOrgTranslationShort(orgTranslationShort);
        organization.setOrgTranslation(orgTranslation);
        organization.setInactive(inactive);

        UCSBOrganization savedOrganizations = ucsbOrganizationRepository.save(organization);

        return savedOrganizations;
    }

    /**
     * Delete an organization. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode code of the organization
     * @return a message indiciating the organization was deleted
     */
    // @Operation(summary= "Delete a UCSBOrganization")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @DeleteMapping("")
    // public Object deleteOrganization(
    //         @Parameter(name="orgCode") @RequestParam String orgCode) {
    //     UCSBOrganization organization = ucsbOrganizationRepository.findById(orgCode)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));

    //     ucsbOrganizationRepository.delete(organization);
    //     return genericMessage("UCSBOrganization with id %s deleted".formatted(code));
    // }

    /**
     * Update a single organization. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode code of the organization
     * @param incoming the new organization contents
     * @return the updated organization object
     */
    // @Operation(summary= "Update a single organization")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    // @PutMapping("")
    // public UCSBOrganization updateOrganization(
    //         @Parameter(name="orgCode") @RequestParam String orgCode,
    //         @RequestBody @Valid UCSBOrganization incoming) {

    //     UCSBOrganization organization = ucsbOrganizationRepository.findById(orgCode)
    //             .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));


    //     organization.setOrgCode(incoming.getOrgCode());
    //     organization.setOrgTranslationShort(incoming.getOrgTranslationShort());
    //     organization.setOrgTranslation(incoming.getOrgTranslation());
    //     organization.setInactive(incoming.getInactive());

    //     UCSBOrganization savedOrganizations = ucsbOrganizationRepository.save(organization);

    //     return savedOrganizations;
    // }
}