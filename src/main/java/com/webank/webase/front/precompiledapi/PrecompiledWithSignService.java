/**
 * Copyright 2014-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.webank.webase.front.precompiledapi;

import static org.fisco.bcos.sdk.contract.precompiled.consensus.ConsensusPrecompiled.FUNC_ADDOBSERVER;
import static org.fisco.bcos.sdk.contract.precompiled.consensus.ConsensusPrecompiled.FUNC_ADDSEALER;
import static org.fisco.bcos.sdk.contract.precompiled.consensus.ConsensusPrecompiled.FUNC_REMOVE;
import static org.fisco.bcos.sdk.contract.precompiled.crud.TablePrecompiled.FUNC_CREATETABLE;
import static org.fisco.bcos.sdk.contract.precompiled.crud.TablePrecompiled.FUNC_INSERT;
import static org.fisco.bcos.sdk.contract.precompiled.crud.TablePrecompiled.FUNC_UPDATE;
import static org.fisco.bcos.sdk.contract.precompiled.sysconfig.SystemConfigPrecompiled.FUNC_SETVALUEBYKEY;

import com.webank.webase.front.base.code.ConstantCode;
import com.webank.webase.front.base.enums.PrecompiledTypes;
import com.webank.webase.front.base.exception.FrontException;
import com.webank.webase.front.base.response.BaseResponse;
import com.webank.webase.front.precompiledapi.crud.Table;
import com.webank.webase.front.transaction.TransService;
import com.webank.webase.front.util.JsonUtils;
import com.webank.webase.front.web3api.Web3ApiService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.contract.precompiled.cns.CNSPrecompiled;
import org.fisco.bcos.sdk.contract.precompiled.crud.TableCRUDService;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Condition;
import org.fisco.bcos.sdk.contract.precompiled.crud.common.Entry;
import org.fisco.bcos.sdk.model.PrecompiledConstant;
import org.fisco.bcos.sdk.model.PrecompiledRetCode;
import org.fisco.bcos.sdk.model.RetCode;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.codec.decode.ReceiptParser;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import static org.fisco.bcos.sdk.contract.precompiled.consensus.ConsensusPrecompiled.FUNC_REMOVE;

/**
 * send raw transaction through webase-sign to call precompiled
 * 
 * @author marsli
 */
@Slf4j
@Service
public class PrecompiledWithSignService {

    @Autowired
    TransService transService;
    @Autowired
    private Web3ApiService web3ApiService;
    public static final Integer NODE_LOWEST_SUPPORT_VERSION_INT = 241;
    public static final String GROUP_FILE_NOT_EXIST = "INEXISTENT";

    /**
     * system config: setValueByKey through webase-sign
     * 
     * @return String result {"code":0,"msg":"success"}
     */
    public String setValueByKey(String groupId, String signUserId, String key, String value) {
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(key);
        funcParams.add(value);
        // get address and abi of precompiled contract
        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.SYSTEM_CONFIG);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.SYSTEM_CONFIG);
        // execute set method
        TransactionReceipt receipt =
                (TransactionReceipt) transService.transHandleWithSign(groupId,
                        signUserId, contractAddress, abiStr, FUNC_SETVALUEBYKEY, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

//    /**
//     * permission: grant through webase-sign
//     *
//     * @return String result {"code":0,"msg":"success"}
//     */
//    public String grant(String groupId, String signUserId, String tableName, String toAddress) {
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(tableName);
//        funcParams.add(toAddress);
//        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.PERMISSION);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.PERMISSION);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, contractAddress, abiStr, FUNC_INSERT, funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    /**
//     * permission: revoke through webase-sign
//     *
//     * @return String result {"code":0,"msg":"success"}
//     */
//    public String revoke(String groupId, String signUserId, String tableName, String toAddress) {
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(tableName);
//        funcParams.add(toAddress);
//        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.PERMISSION);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.PERMISSION);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, contractAddress, abiStr, FUNC_REMOVE, funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    /**
//     * user table permission: grant through webase-sign
//     *
//     * @return String result {"code":0,"msg":"success"}
//     */
//    public String grantWrite(String groupId, String signUserId, String tableName, String toAddress) {
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(tableName);
//        funcParams.add(toAddress);
//        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.PERMISSION);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.PERMISSION);
//        TransactionReceipt receipt =
//            (TransactionReceipt) transService.transHandleWithSign(groupId,
//                signUserId, contractAddress, abiStr, FUNC_GRANTWRITE, funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    /**
//     * user table permission: revoke through webase-sign
//     *
//     * @return String result {"code":0,"msg":"success"}
//     */
//    public String revokeWrite(String groupId, String signUserId, String tableName, String toAddress) {
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(tableName);
//        funcParams.add(toAddress);
//        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.PERMISSION);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.PERMISSION);
//        TransactionReceipt receipt =
//            (TransactionReceipt) transService.transHandleWithSign(groupId,
//                signUserId, contractAddress, abiStr, FUNC_REVOKEWRITE, funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }


    /**
     * consensus: add sealer through webase-sign
     * v1.5.0 增加校验群组文件是否存在，P2P连接存在
     */
    public String addSealer(String groupId, String signUserId, String nodeId, int weight) {
        // check node id
        if (!isValidNodeID(groupId, nodeId)) {
            return PrecompiledRetCode.CODE_INVALID_NODEID.toString();
        }
        // trans
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(nodeId);
        funcParams.add(weight);
        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CONSENSUS);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CONSENSUS);
        TransactionReceipt receipt =
                (TransactionReceipt) transService.transHandleWithSign(groupId,
                        signUserId, contractAddress, abiStr, FUNC_ADDSEALER, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

    /**
     * consensus: add observer through webase-sign
     */
    public String addObserver(String groupId, String signUserId, String nodeId) {
        // check node id
        if (!isValidNodeID(groupId, nodeId)) {
            return PrecompiledRetCode.CODE_INVALID_NODEID.toString();
        }
        List<String> observerList = web3ApiService.getObserverList(groupId);
        if (observerList.contains(nodeId)) {
            return ConstantCode.ALREADY_EXISTS_IN_OBSERVER_LIST.toString();
        }

        // trans
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(nodeId);
        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CONSENSUS);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CONSENSUS);
        TransactionReceipt receipt =
                (TransactionReceipt) transService.transHandleWithSign(groupId,
                        signUserId, contractAddress, abiStr, FUNC_ADDOBSERVER, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

    /**
     * consensus: remove node from list through webase-sign
     */
    public String removeNode(String groupId, String signUserId, String nodeId) {
//        List<String> groupPeers = web3ApiService.getGroupPeers(groupId);
//        if (!groupPeers.contains(nodeId)) {
//            return ConstantCode.ALREADY_REMOVED_FROM_THE_GROUP.toString();
//        }
        // trans
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(nodeId);
        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CONSENSUS);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CONSENSUS);
        TransactionReceipt receipt = new TransactionReceipt();
        try {
            receipt = (TransactionReceipt) transService.transHandleWithSign(groupId,
                    signUserId, contractAddress, abiStr, FUNC_REMOVE, funcParams, false);
        } catch (RuntimeException e) {
            // firstly remove node that sdk connected to the node, return the request that present
            // susscces
            // because the exception is throwed by getTransactionReceipt, we need ignore it.
            if (e.getMessage().contains("Don't send requests to this group")) {
                return ConstantCode.ALREADY_REMOVED_FROM_THE_GROUP.toString();
            } else {
                throw e;
            }
        }
        return this.handleTransactionReceipt(receipt);
    }

    /**
     * check node id
     */
    private boolean isValidNodeID(String groupId, String _nodeID) {
        boolean flag = false;
        List<String> nodeIDs = web3ApiService.getGroupPeers(groupId);
        for (String nodeID : nodeIDs) {
            if (_nodeID.equals(nodeID)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * CRUD: create table through webase-sign
     */
    public String createTable(String groupId, String signUserId, Table table) {
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(table.getTableName());
        funcParams.add(table.getKey());
        String valueFieldsString = TableCRUDService.convertValueFieldsToString(table.getValueFields());
        funcParams.add(valueFieldsString);
        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.TABLE_FACTORY);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.TABLE_FACTORY);
        TransactionReceipt receipt =
                (TransactionReceipt) transService.transHandleWithSign(groupId,
                        signUserId, contractAddress, abiStr, FUNC_CREATETABLE, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

    /**
     * CRUD: insert table through webase-sign
     */
    public String insert(String groupId, String signUserId, Table table, Entry entry) {
        checkTableKeyLength(table);
        // trans
//        String entryJsonStr;
//        try {
//            entryJsonStr =
//                    ObjectMapperFactory.getObjectMapper().writeValueAsString(entry.getFieldNameToValue());
//        } catch (JsonProcessingException e) {
//            log.error("remove JsonProcessingException:[]", e);
//            throw new FrontException(ConstantCode.CRUD_PARSE_CONDITION_ENTRY_FIELD_JSON_ERROR);
//        }

        log.debug(
            "table getKey : {},  table getTableName :{},table getKeyFieldName :{},table getValueFields: {} ",
            table.getKey(), table.getTableName(),
            table.getKeyFieldName(), table.getValueFields());
        log.info("entry is {}", JsonUtils.objToString(entry.getFieldNameToValue()));

        List<Object> funcParams = new ArrayList<>();
        funcParams.add(table.getTableName());
        funcParams.add(entry.getTablePrecompiledEntry());
        log.debug("funcParams is {}",  JsonUtils.objToString(funcParams));

        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CRUD);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CRUD);
        log.debug("abiStr is {}", JsonUtils.objToString(abiStr));
        log.debug("contractAddress is {}", contractAddress);
        TransactionReceipt receipt =
            (TransactionReceipt) transService.transHandleWithSign(groupId,
                signUserId, contractAddress, abiStr, FUNC_INSERT, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

    /**
     * CRUD: update table through webase-sign
     */
    public String update(String groupId, String signUserId, Table table, Entry entry,
            Condition condition) {
        checkTableKeyLength(table);
        // trans
//        String entryJsonStr, conditionStr;
//        try {
//            entryJsonStr =
//                    ObjectMapperFactory.getObjectMapper().writeValueAsString(entry.getFieldNameToValue());
//            conditionStr = ObjectMapperFactory.getObjectMapper()
//                    .writeValueAsString(condition.getConditions());
//        } catch (JsonProcessingException e) {
//            log.error("update JsonProcessingException:[]", e);
//            throw new FrontException(ConstantCode.CRUD_PARSE_CONDITION_ENTRY_FIELD_JSON_ERROR);
//        }
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(table.getTableName());
        funcParams.add(table.getKey());
        funcParams.add(table.getOptional());
        funcParams.add(condition);
        funcParams.add(entry.getTablePrecompiledEntry());

        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CRUD);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CRUD);
        TransactionReceipt receipt =
                (TransactionReceipt) transService.transHandleWithSign(groupId,
                        signUserId, contractAddress, abiStr, FUNC_UPDATE, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

    /**
     * CRUD: remove table through webase-sign
     */
    public String remove(String groupId, String signUserId, Table table, Condition condition) {
        checkTableKeyLength(table);
        // trans
//        String conditionStr;
//        try {
//            conditionStr = ObjectMapperFactory.getObjectMapper()
//                    .writeValueAsString(condition.getConditions());
//        } catch (JsonProcessingException e) {
//            log.error("remove JsonProcessingException:[]", e);
//            throw new FrontException(ConstantCode.CRUD_PARSE_CONDITION_ENTRY_FIELD_JSON_ERROR);
//        }
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(table.getTableName());
        funcParams.add(table.getKey());
        funcParams.add(condition);
        funcParams.add(table.getOptional());
        String contractAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CRUD);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CRUD);
        TransactionReceipt receipt =
                (TransactionReceipt) transService.transHandleWithSign(groupId,
                        signUserId, contractAddress, abiStr, FUNC_REMOVE, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

    private void checkTableKeyLength(Table table) {
        if (table.getKey().length() > PrecompiledConstant.TABLE_KEY_MAX_LENGTH) {
            throw new FrontException(ConstantCode.CRUD_TABLE_KEY_LENGTH_ERROR.getCode(),
                    "The value of the table key exceeds the maximum limit("
                            + PrecompiledConstant.TABLE_KEY_MAX_LENGTH + ").");
        }
    }

//
//    /**
//     * chain governance, above FISCO-BCOS v2.5.0
//     */
//    public String grantChainCommittee(String groupId, String signUserId, String toAddress) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(toAddress);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_GRANTCOMMITTEEMEMBER,
//                        funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    public String revokeChainCommittee(String groupId, String signUserId, String toAddress) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(toAddress);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_REVOKECOMMITTEEMEMBER,
//                        funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    public String updateChainCommitteeWeight(String groupId, String signUserId, String toAddress,
//            int weight) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(toAddress);
//        funcParams.add(weight);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_UPDATECOMMITTEEMEMBERWEIGHT,
//                        funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    public String updateThreshold(String groupId, String signUserId, int threshold) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(threshold);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_UPDATETHRESHOLD,
//                        funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    public String grantOperator(String groupId, String signUserId, String toAddress) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(toAddress);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_GRANTOPERATOR, funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    public String revokeOperator(String groupId, String signUserId, String toAddress) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(toAddress);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_REVOKEOPERATOR, funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    public String freezeAccount(String groupId, String signUserId, String toAddress) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(toAddress);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_FREEZEACCOUNT, funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }
//
//    public String unfreezeAccount(String groupId, String signUserId, String toAddress) {
//        // trans
//        List<Object> funcParams = new ArrayList<>();
//        funcParams.add(toAddress);
//        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CHAIN_GOVERN);
//        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CHAIN_GOVERN);
//        TransactionReceipt receipt =
//                (TransactionReceipt) transService.transHandleWithSign(groupId,
//                        signUserId, precompiledAddress, abiStr, FUNC_UNFREEZEACCOUNT,
//                        funcParams);
//        return this.handleTransactionReceipt(receipt);
//    }

    public String registerCns(String groupId, String signUserId, String cnsContractName, String version,
            String contractAddress, String abiInfo) {
        // trans
        List<Object> funcParams = new ArrayList<>();
        funcParams.add(cnsContractName);
        funcParams.add(version);
        funcParams.add(contractAddress);
        funcParams.add(abiInfo);
        String precompiledAddress = PrecompiledCommonInfo.getAddress(PrecompiledTypes.CNS);
        String abiStr = PrecompiledCommonInfo.getAbi(PrecompiledTypes.CNS);
        TransactionReceipt receipt =
                (TransactionReceipt) transService.transHandleWithSign(groupId,
                        signUserId, precompiledAddress, abiStr, CNSPrecompiled.FUNC_INSERT, funcParams, false);
        return this.handleTransactionReceipt(receipt);
    }

    /**
     * handle receipt of precompiled
     * @related: PrecompiledRetCode and ReceiptParser
     * return: {"code":1,"msg":"Success"} => {"code":0,"message":"Success"}
     */
    private String handleTransactionReceipt(TransactionReceipt receipt) {
        log.debug("handle tx receipt of precompiled");
        try {
            RetCode sdkRetCode = ReceiptParser.parseTransactionReceipt(receipt);
            log.info("handleTransactionReceipt sdkRetCode:{}", sdkRetCode);
            if (sdkRetCode.getCode() >= 0) {
                return new BaseResponse(ConstantCode.RET_SUCCESS, sdkRetCode.getMessage()).toString();
            } else {
                throw new FrontException(sdkRetCode.getCode(), sdkRetCode.getMessage());
            }
        } catch (ContractException e) {
            log.error("handleTransactionReceipt e:[]", e);
            throw new FrontException(e.getErrorCode(), e.getMessage());
        }
    }


}
