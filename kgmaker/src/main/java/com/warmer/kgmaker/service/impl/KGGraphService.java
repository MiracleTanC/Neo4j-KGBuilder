package com.warmer.kgmaker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.warmer.kgmaker.dal.IKGraphRepository;
import com.warmer.kgmaker.entity.QAEntityItem;
import com.warmer.kgmaker.query.GraphQuery;
import com.warmer.kgmaker.service.IKGGraphService;
import com.warmer.kgmaker.util.GraphPageRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KGGraphService implements IKGGraphService {

    @Autowired
    @Qualifier("KGraphRepository")
    private IKGraphRepository kgRepository;
    
    @Override
	public GraphPageRecord<HashMap<String, Object>> getPageDomain(GraphQuery queryItem) {
		return kgRepository.getPageDomain(queryItem);
	}
    @Override
    public void deleteKGdomain(String domain) {
        kgRepository.deleteKGdomain(domain);
    }

    @Override
    public HashMap<String, Object> getdomaingraph(GraphQuery query) {
        return kgRepository.getdomaingraph(query);
    }

    @Override
    public HashMap<String, Object> getdomainnodes(String domain, Integer pageIndex, Integer pageSize) {
        return kgRepository.getdomainnodes(domain, pageIndex, pageSize);
    }

    @Override
    public long getrelationnodecount(String domain, long nodeid) {
        return kgRepository.getrelationnodecount(domain, nodeid);
    }

    @Override
    public void createdomain(String domain) {
        kgRepository.createdomain(domain);
    }

    @Override
    public HashMap<String, Object> getmorerelationnode(String domain, String nodeid) {
        return kgRepository.getmorerelationnode(domain, nodeid);
    }

    @Override
    public HashMap<String, Object> updatenodename(String domain, String nodeid, String nodename) {
        return kgRepository.updatenodename(domain, nodeid, nodename);
    }

    @Override
    public HashMap<String, Object> createnode(String domain, QAEntityItem entity) {
        return kgRepository.createnode(domain, entity);
    }

    @Override
    public HashMap<String, Object> batchcreatenode(String domain, String sourcename, String relation,
                                                   String[] targetnames) {
        return kgRepository.batchcreatenode(domain, sourcename, relation, targetnames);
    }

    @Override
    public HashMap<String, Object> batchcreatechildnode(String domain, String sourceid, Integer entitytype,
                                                        String[] targetnames, String relation) {
        return kgRepository.batchcreatechildnode(domain, sourceid, entitytype, targetnames, relation);
    }

    @Override
    public List<HashMap<String, Object>> batchcreatesamenode(String domain, Integer entitytype, String[] sourcenames) {
        return kgRepository.batchcreatesamenode(domain, entitytype, sourcenames);
    }

    @Override
    public HashMap<String, Object> createlink(String domain, long sourceid, long targetid, String ship) {
        return kgRepository.createlink(domain, sourceid, targetid, ship);
    }

    @Override
    public HashMap<String, Object> updatelink(String domain, long shipid, String shipname) {
        return kgRepository.updatelink(domain, shipid, shipname);
    }

    @Override
    public List<HashMap<String, Object>> deletenode(String domain, long nodeid) {
        return kgRepository.deletenode(domain, nodeid);
    }

    @Override
    public void deletelink(String domain, long shipid) {
        kgRepository.deletelink(domain, shipid);
    }

    @Override
    public HashMap<String, Object> createGraphByText(String domain, Integer entitytype, Integer operatetype,
                                                     Integer sourceid, String[] rss) {
        return kgRepository.createGraphByText(domain, entitytype, operatetype, sourceid, rss);
    }

    @Override
    public void batchcreateGraph(String domain, List<Map<String, Object>> params) {
        kgRepository.batchcreateGraph(domain, params);
    }

    @Override
    public void updateNodeFileStatus(String domain, long nodeId, int status) {
        kgRepository.updateNodeFileStatus(domain,nodeId,status);
    }

    @Override
    public void updateCorrdOfNode(String domain, String uuid, Double fx, Double fy) {
        kgRepository.updateCorrdOfNode(domain,uuid,fx,fy);
    }

    @Override
	public void batchInsertByCSV(String domain, String csvUrl, int status) {
		kgRepository.batchInsertByCSV(domain, csvUrl, status);
	}

	

}
