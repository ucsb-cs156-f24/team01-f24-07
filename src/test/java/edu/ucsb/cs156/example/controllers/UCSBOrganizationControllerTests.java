package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationController.class)
@Import(TestConfig.class)
public class UCSBOrganizationControllerTests extends ControllerTestCase {
	@MockBean
	UCSBOrganizationRepository ucsbOrganizationRepository;

	@MockBean
	UserRepository userRepository;

	// Authorization tests for /api/ucsborganization/admin/all

	@Test
	public void logged_out_users_cannot_get_all() throws Exception {
			mockMvc.perform(get("/api/ucsborganization/all"))
							.andExpect(status().is(403)); // logged out users can't get all
	}

	@WithMockUser(roles = { "USER" })
	@Test
	public void logged_in_users_can_get_all() throws Exception {
			mockMvc.perform(get("/api/ucsborganization/all"))
							.andExpect(status().is(200)); // logged
	}

	@Test
	public void logged_out_users_cannot_get_by_id() throws Exception {
			mockMvc.perform(get("/api/ucsborganization?orgCode=acm"))
							.andExpect(status().is(403)); // logged out users can't get by id
	}

	// Authorization tests for /api/ucsborganization/post
	// (Perhaps should also have these for put and delete)

	@Test
	public void logged_out_users_cannot_post() throws Exception {
			mockMvc.perform(post("/api/ucsborganization/post"))
							.andExpect(status().is(403));
	}

	@WithMockUser(roles = { "USER" })
	@Test
	public void logged_in_regular_users_cannot_post() throws Exception {
			mockMvc.perform(post("/api/ucsborganization/post"))
							.andExpect(status().is(403)); // only admins can post
	}

	@WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                UCSBOrganization sky = UCSBOrganization.builder()
							.orgCode("SKY")
							.orgTranslationShort("SKYDIVING CLUB")
							.orgTranslation("SKYDIVING CLUB AT UCSB")
							.inactive(false)
							.build();

                when(ucsbOrganizationRepository.findById(eq("SKY"))).thenReturn(Optional.of(sky));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=SKY"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("SKY"));
                String expectedJson = mapper.writeValueAsString(sky);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbOrganizationRepository.findById(eq("OSLI"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganization?orgCode=OSLI"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbOrganizationRepository, times(1)).findById(eq("OSLI"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganization with id OSLI not found", json.get("message"));
        }

	@WithMockUser(roles = { "USER" })
	@Test
	public void logged_in_user_can_get_all_ucsborganization() throws Exception {

			// arrange

			UCSBOrganization zpr = UCSBOrganization.builder()
							.orgCode("ZPR")
							.orgTranslationShort("ZETA PHI RHO")
							.orgTranslation("ZETA PHI RHO")
							.inactive(false)
							.build();

			UCSBOrganization sky = UCSBOrganization.builder()
							.orgCode("SKY")
							.orgTranslationShort("SKYDIVING CLUB")
							.orgTranslation("SKYDIVING CLUB AT UCSB")
							.inactive(false)
							.build();

			ArrayList<UCSBOrganization> expectedOrgs = new ArrayList<>();
			expectedOrgs.addAll(Arrays.asList(zpr, sky));

			when(ucsbOrganizationRepository.findAll()).thenReturn(expectedOrgs);

			// act
			MvcResult response = mockMvc.perform(get("/api/ucsborganization/all"))
							.andExpect(status().isOk()).andReturn();

			// assert

			verify(ucsbOrganizationRepository, times(1)).findAll();
			String expectedJson = mapper.writeValueAsString(expectedOrgs);
			String responseString = response.getResponse().getContentAsString();
			assertEquals(expectedJson, responseString);
	}

	@WithMockUser(roles = { "ADMIN", "USER" })
	@Test
	public void an_admin_user_can_post_a_new_org() throws Exception {
			// arrange

			UCSBOrganization krc = UCSBOrganization.builder()
							.orgCode("KRC")
							.orgTranslationShort("KOREAN RADIO CL")
							.orgTranslation("KOREAN RADIO CLUB")
							.inactive(true)
							.build();

			when(ucsbOrganizationRepository.save(eq(krc))).thenReturn(krc);

			// act
			MvcResult response = mockMvc.perform(
							post("/api/ucsborganization/post?orgCode=KRC&orgTranslationShort=KOREAN RADIO CL&orgTranslation=KOREAN RADIO CLUB&inactive=true")
											.with(csrf()))
							.andExpect(status().isOk()).andReturn();

			// assert
			verify(ucsbOrganizationRepository, times(1)).save(krc);
			String expectedJson = mapper.writeValueAsString(krc);
			String responseString = response.getResponse().getContentAsString();
			// {"orgCode":"KRC","orgTranslationShort":"KOREAN RADIO CL","orgTranslation":"KOREAN RADIO CLUB","inactive":false}
			// {"orgCode":"KRC","orgTranslationShort":"KOREAN RADIO CL","orgTranslation":"KOREAN RADIO CLUB","inactive":false}
			// assertEquals("expectedJson", expectedJson);
			assertEquals(expectedJson, responseString);
	}

	@WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_date() throws Exception {
                // arrange

                UCSBOrganization krc = UCSBOrganization.builder()
							.orgCode("KRC")
							.orgTranslationShort("KOREAN RADIO CL")
							.orgTranslation("KOREAN RADIO CLUB")
							.inactive(true)
							.build();

                when(ucsbOrganizationRepository.findById(eq("KRC"))).thenReturn(Optional.of(krc));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganization?orgCode=KRC")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("KRC");
                verify(ucsbOrganizationRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id KRC deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_commons_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbOrganizationRepository.findById(eq("klio"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganization?orgCode=klio")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationRepository, times(1)).findById("klio");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganization with id klio not found", json.get("message"));
        }

	@WithMockUser(roles = { "ADMIN", "USER" })
	@Test
	public void admin_can_edit_an_existing_organization() throws Exception {
		// arrange

		UCSBOrganization zprOrig = UCSBOrganization.builder()
							.orgCode("OSLI")
							.orgTranslationShort("STUDENT LIFE")
							.orgTranslation("OFFICE OF STUDENT LIFE")
							.inactive(true)
							.build();
		
		UCSBOrganization zprEdited = UCSBOrganization.builder()
								.orgCode("osli")
								.orgTranslationShort("STUD LIFE")
								.orgTranslation("OFFICE OF STUDENT LIFE at UCSB")
								.inactive(false)
								.build();

		String requestBody = mapper.writeValueAsString(zprEdited);

		when(ucsbOrganizationRepository.findById(eq("OSLI"))).thenReturn(Optional.of(zprOrig));

		// act
		MvcResult response = mockMvc.perform(
						put("/api/ucsborganization?orgCode=OSLI")
										.contentType(MediaType.APPLICATION_JSON)
										.characterEncoding("utf-8")
										.content(requestBody)
										.with(csrf()))
						.andExpect(status().isOk()).andReturn();

		// assert
		verify(ucsbOrganizationRepository, times(1)).findById("OSLI");
		verify(ucsbOrganizationRepository, times(1)).save(zprEdited); // should be saved with updated info
		String responseString = response.getResponse().getContentAsString();
		assertEquals(requestBody, responseString);
	}

	@WithMockUser(roles = { "ADMIN", "USER" })
	@Test
	public void admin_cannot_edit_organization_that_does_not_exist() throws Exception {
		// arrange

		UCSBOrganization zprEdited = UCSBOrganization.builder()
							.orgCode("ZPR")
							.orgTranslationShort("ZETA PHI RHO")
							.orgTranslation("ZETA PHI RHO at UCSB")
							.inactive(true)
							.build();

		String requestBody = mapper.writeValueAsString(zprEdited);

		when(ucsbOrganizationRepository.findById(eq("ZPR"))).thenReturn(Optional.empty());

		// act
		MvcResult response = mockMvc.perform(
						put("/api/ucsborganization?orgCode=ZPR")
										.contentType(MediaType.APPLICATION_JSON)
										.characterEncoding("utf-8")
										.content(requestBody)
										.with(csrf()))
						.andExpect(status().isNotFound()).andReturn();

		// assert
		verify(ucsbOrganizationRepository, times(1)).findById("ZPR");
		Map<String, Object> json = responseToJson(response);
		assertEquals("UCSBOrganization with id ZPR not found", json.get("message"));
	}
}
