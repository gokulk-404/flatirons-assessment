package org.gk.flatirons.assessment.ims.service;

import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.SchedulingConflictException;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;
import org.gk.flatirons.assessment.ims.constant.InterviewerDepartment;
import org.gk.flatirons.assessment.ims.dto.request.BulkInterviewerCreateRequest;
import org.gk.flatirons.assessment.ims.dto.request.CreateInterviewerRequest;
import org.gk.flatirons.assessment.ims.dto.response.InterviewerDetail;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.gk.flatirons.assessment.ims.repository.InterviewRepository;
import org.gk.flatirons.assessment.ims.repository.InterviewerRepository;
import org.gk.flatirons.assessment.ims.utils.ResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewerServiceTest {

    @Mock
    private InterviewerRepository interviewerRepository;
    @Mock
    private InterviewRepository interviewRepository;
    @Mock
    private ResponseMapper responseMapper;

    @InjectMocks
    private InterviewerService interviewerService;

    @Captor
    private ArgumentCaptor<List<Integer>> idsCaptor;
    @Captor
    private ArgumentCaptor<Interviewer> interviewerCaptor;

    private static final Instant START = Instant.now().plus(1, ChronoUnit.DAYS);
    private static final Instant END = START.plus(1, ChronoUnit.HOURS);

    private Interviewer interviewer(Integer id, String name, String email) {
        Interviewer i = Interviewer.builder()
                .fullName(name)
                .email(email)
                .department(InterviewerDepartment.ENGINEERING)   // use a real constant
                .build();
        ReflectionTestUtils.setField(i, "id", id);               // id has no setter
        return i;
    }

    private CreateInterviewerRequest createRequest(String name, String email) {
        return new CreateInterviewerRequest(name, email, InterviewerDepartment.ENGINEERING);
    }

    @Nested
    @DisplayName("fetchAllInterviewersWithCountAndConflictValidation")
    class FetchAll {

        @Test
        @DisplayName("returns interviewers when all exist and there is no conflict")
        void returnsInterviewers_whenAllFoundAndNoConflict() {
            Set<Integer> ids = Set.of(1, 2);
            List<Interviewer> found = List.of(interviewer(1, "Ravi", "ravi@x.com"),
                    interviewer(2, "Priya", "priya@x.com"));
            when(interviewerRepository.findAllByIdForInterviewSchedule(ids)).thenReturn(found);
            when(interviewRepository.existsInterviewerConflict(anyList(), eq(InterviewStatus.SCHEDULED), eq(START), eq(END)))
                    .thenReturn(false);

            List<Interviewer> result =
                    interviewerService.fetchAllInterviewersWithCountAndConflictValidation(ids, START, END);

            assertThat(result).containsExactlyElementsOf(found);
        }

        @Test
        @DisplayName("passes the right ids, status and time window to the conflict query")
        void passesCorrectArgumentsToConflictQuery() {
            Set<Integer> ids = Set.of(1, 2);
            when(interviewerRepository.findAllByIdForInterviewSchedule(ids))
                    .thenReturn(List.of(interviewer(1, "Ravi", "ravi@x.com"),
                            interviewer(2, "Priya", "priya@x.com")));
            when(interviewRepository.existsInterviewerConflict(anyList(), any(), any(), any())).thenReturn(false);

            interviewerService.fetchAllInterviewersWithCountAndConflictValidation(ids, START, END);

            verify(interviewRepository).existsInterviewerConflict(
                    idsCaptor.capture(), eq(InterviewStatus.SCHEDULED), eq(START), eq(END));
            assertThat(idsCaptor.getValue()).containsExactlyInAnyOrder(1, 2);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when some interviewers are missing")
        void throwsNotFound_whenSomeMissing() {
            Set<Integer> ids = Set.of(1, 2, 3);
            when(interviewerRepository.findAllByIdForInterviewSchedule(ids))
                    .thenReturn(List.of(interviewer(1, "Ravi", "ravi@x.com")));   // 2 and 3 missing

            assertThatThrownBy(() ->
                    interviewerService.fetchAllInterviewersWithCountAndConflictValidation(ids, START, END))
                    .isInstanceOf(ResourceNotFoundException.class);

            // fails fast: the conflict check must not run
            verifyNoInteractions(interviewRepository);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when none are found")
        void throwsNotFound_whenNoneFound() {
            Set<Integer> ids = Set.of(10);
            when(interviewerRepository.findAllByIdForInterviewSchedule(ids)).thenReturn(List.of());

            assertThatThrownBy(() ->
                    interviewerService.fetchAllInterviewersWithCountAndConflictValidation(ids, START, END))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(interviewRepository);
        }

        @Test
        @DisplayName("throws SchedulingConflictException when an interviewer is already booked")
        void throwsConflict_whenSlotTaken() {
            Set<Integer> ids = Set.of(1);
            when(interviewerRepository.findAllByIdForInterviewSchedule(ids))
                    .thenReturn(List.of(interviewer(1, "Ravi", "ravi@x.com")));
            when(interviewRepository.existsInterviewerConflict(anyList(), eq(InterviewStatus.SCHEDULED), eq(START), eq(END)))
                    .thenReturn(true);

            assertThatThrownBy(() ->
                    interviewerService.fetchAllInterviewersWithCountAndConflictValidation(ids, START, END))
                    .isInstanceOf(SchedulingConflictException.class)
                    .hasMessage("An interviewer already has an interview in this time slot");
        }
    }

    @Nested
    @DisplayName("createNewInterviewer")
    class CreateOne {

        @Test
        @DisplayName("saves an interviewer built from the request and returns the mapped detail")
        void savesAndMaps() {
            CreateInterviewerRequest request = createRequest("Ravi Kumar", "ravi@x.com");
            Interviewer saved = interviewer(1, "Ravi Kumar", "ravi@x.com");
            InterviewerDetail detail = new InterviewerDetail(1, "Ravi Kumar", "ravi@x.com", InterviewerDepartment.ENGINEERING);

            when(interviewerRepository.save(any(Interviewer.class))).thenReturn(saved);
            when(responseMapper.mapToInterviewerDetailDto(saved)).thenReturn(detail);

            InterviewerDetail result = interviewerService.createNewInterviewer(request);

            assertThat(result).isEqualTo(detail);

            verify(interviewerRepository).save(interviewerCaptor.capture());
            Interviewer toSave = interviewerCaptor.getValue();
            assertThat(toSave.getFullName()).isEqualTo("Ravi Kumar");
            assertThat(toSave.getEmail()).isEqualTo("ravi@x.com");
            assertThat(toSave.getDepartment()).isEqualTo(InterviewerDepartment.ENGINEERING);
        }
    }

    @Nested
    @DisplayName("createNewInterviewersBulk")

    class CreateBulk {

        @Test
        @DisplayName("saves every interviewer and returns details in the same order")
        void savesAllAndPreservesOrder() {
            CreateInterviewerRequest r1 = createRequest("Ravi", "ravi@x.com");
            CreateInterviewerRequest r2 = createRequest("Priya", "priya@x.com");
            BulkInterviewerCreateRequest bulk = new BulkInterviewerCreateRequest(List.of(r1, r2));

            Interviewer s1 = interviewer(1, "Ravi", "ravi@x.com");
            Interviewer s2 = interviewer(2, "Priya", "priya@x.com");
            InterviewerDetail d1 = new InterviewerDetail(1, "Ravi", "ravi@x.com", InterviewerDepartment.ENGINEERING);
            InterviewerDetail d2 = new InterviewerDetail(2, "Priya", "priya@x.com", InterviewerDepartment.ENGINEERING);

            when(interviewerRepository.save(any(Interviewer.class))).thenReturn(s1, s2);
            when(responseMapper.mapToInterviewerDetailDto(s1)).thenReturn(d1);
            when(responseMapper.mapToInterviewerDetailDto(s2)).thenReturn(d2);

            List<InterviewerDetail> result = interviewerService.createNewInterviewersBulk(bulk);

            assertThat(result).containsExactly(d1, d2);
            verify(interviewerRepository, times(2)).save(interviewerCaptor.capture());
            assertThat(interviewerCaptor.getAllValues())
                    .extracting(Interviewer::getEmail)
                    .containsExactly("ravi@x.com", "priya@x.com");
        }

        @Test
        @DisplayName("returns an empty list and saves nothing for an empty request")
        void emptyRequest() {
            List<InterviewerDetail> result =
                    interviewerService.createNewInterviewersBulk(new BulkInterviewerCreateRequest(List.of()));

            assertThat(result).isEmpty();
            verify(interviewerRepository, never()).save(any());
        }
    }
}