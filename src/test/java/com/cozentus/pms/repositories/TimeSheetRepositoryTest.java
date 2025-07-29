//
//package com.cozentus.pms.repositories;
//
//import com.cozentus.pms.dto.TimesheetFlatDTO;
//import com.cozentus.pms.dto.TimesheetForManagerFlatDTO;
//import com.cozentus.pms.dto.TimesheetSummaryDTO;
//import com.cozentus.pms.entites.TimeSheet;
//import com.cozentus.pms.helpers.ApprovalStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//public class TimeSheetRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private TimeSheetRepository timeSheetRepository;
//
//    @Test
//    public void testFindAllTimesheetByEmpIdAndDateBetween() {
//        // setup data
//        TimeSheet timeSheet = new TimeSheet();
//        // set properties for timeSheet
//        entityManager.persist(timeSheet);
//        entityManager.flush();
//
//        // execute
//        List<TimesheetFlatDTO> result = timeSheetRepository.findAllTimesheetByEmpIdAndDateBetween("empId", LocalDate.now(), LocalDate.now(), "projectCode");
//
//        // verify
//        assertThat(result).isNotEmpty();
//        // add more assertions based on your requirements
//    }
//
//    // similar tests for other methods
//}
