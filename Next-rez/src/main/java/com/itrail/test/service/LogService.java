package com.itrail.test.service;

import com.itrail.test.app.model.FilterLog;
import com.itrail.test.app.model.LogView;
import com.itrail.test.domain.BaseResponse;
import com.itrail.test.exception.mapper.ItException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author barysevich_k
 */

@Stateless
public class LogService {
    
    private static final Logger LOGGER = LogManager.getLogger(LogService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    protected void init() {
    }

    @PreDestroy
    protected void destroy() {
    }

    public List<LogView> getAllLog() {
        return entityManager.createQuery("SELECT e FROM  LogView e").getResultList();
    }

    public void createLog(LogView... logi) {
        createLog(Arrays.asList(logi));
    }

    @Transactional
    public void createLog(List<LogView> logi) {
        logi.stream().forEach(s -> entityManager.merge(s));
    }

    public void createLog(LogView logi) {
        entityManager.merge(logi);
    }


    public BaseResponse<List<LogView>> getFoundLog(FilterLog filterLog) throws Exception {
        BaseResponse<List<LogView>> f = new BaseResponse(0, "success"); 
//        f.setData(entityManager.createQuery("SELECT e FROM LogView e WHERE e.date BETWEEN :dateFromFilter AND :dateToFilter AND e.levels = :infoFilter")
//                //.setParameter("idFilter", filterLog.getId())
//                .setParameter("dateFromFilter", filterLog.getDateFrom())
//                .setParameter("dateToFilter", filterLog.getDateTo())
//                .setParameter("infoFilter", filterLog.getInfo())
//                .setMaxResults(filterLog.getLimit())
//                .setFirstResult(filterLog.getOffset())
//                .getResultList());     
        try{
        f.setData(entityManager.createNativeQuery("SELECT * from LOGGERSTABLEs a where a.levels = ? AND a.date between ? and ?;")
                                            .setParameter(2, filterLog.getDateFrom())
                                            .setParameter(3, filterLog.getDateTo())
                                            .setParameter(1, filterLog.getInfo())
                                            .getResultList()); //через SQL 
        }catch(Exception e){
            LOGGER.error(e.getMessage());
            LOGGER.trace(Arrays.toString(e.getStackTrace()));
            throw new SQLException();  
        }
        return f;
    }   
}
