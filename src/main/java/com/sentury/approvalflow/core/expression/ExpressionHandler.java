package com.sentury.approvalflow.core.expression;

import com.sentury.approvalflow.common.constants.NodeUserTypeEnum;
import com.sentury.approvalflow.common.constants.ProcessInstanceConstant;
import com.sentury.approvalflow.common.dto.flow.Condition;
import com.sentury.approvalflow.common.dto.flow.NodeUser;
import com.sentury.approvalflow.common.dto.flow.SelectValue;
import com.sentury.approvalflow.common.dto.third.DeptDto;
import com.sentury.approvalflow.common.dto.third.UserDto;
import com.sentury.approvalflow.common.utils.JsonUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.sentury.approvalflow.core.expression.condition.NodeExpressionResultStrategyFactory;
import com.sentury.approvalflow.core.node.NodeDataStoreFactory;
import com.sentury.approvalflow.core.utils.BizHttpUtil;
import com.sentury.approvalflow.core.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表达式解析
 */
@Component("expressionHandler")
@Slf4j
public class ExpressionHandler {


    /**
     * 日期时间比较
     *
     * @param key
     * @param symbol
     * @param param
     * @param execution
     * @param format    时间格式化模式
     * @return
     */
    public boolean dateTimeHandler(String key, String symbol, Object param, DelegateExecution execution, String format) {


        return dateTimeHandler(key, symbol, param, execution.getVariable(key), format);

    }

    public boolean dateTimeHandler(String key, String symbol, Object param, Object value, String format) {


        log.debug("表单值：key={} value={}", key, JsonUtil.toJSONString(value));
        log.debug("条件 标识:{} 参数：{} 格式：{}", symbol, JsonUtil.toJSONString(param), format);

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EMPTY)) {

            return value == null || StrUtil.isBlankIfStr(value);

        }
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EMPTY)) {

            return value != null && !StrUtil.isBlankIfStr(value);

        }
        //表单值为空
        if (value == null) {
            return false;
        }

        long valueTime = DateUtil.parse(value.toString(), format).getTime();
        long paramTime = DateUtil.parse(param.toString(), format).getTime();


        return compare(StrUtil.format("${key{}{}}", symbol, paramTime), Dict.create().set("key", valueTime));
    }




    /**
     * 数字类型比较
     *
     * @param key       表单key
     * @param symbol    比较符号
     * @param param     表单参数
     * @param execution
     * @return
     */
    public boolean numberHandler(String key, String symbol, Object param, DelegateExecution execution) {

        return numberHandler(key, symbol, param, execution.getVariable(key));


    }


    public boolean numberHandler(String key, String symbol, Object param, Object value) {


        log.debug("表单值：key={} value={}", key, JsonUtil.toJSONString(value));
        log.debug("条件 标识:{} 参数：{}", symbol, JsonUtil.toJSONString(param));

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EMPTY)) {

            return value == null || StrUtil.isBlankIfStr(value);

        }
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EMPTY)) {

            return value != null && !StrUtil.isBlankIfStr(value);

        }

        //表单值为空
        if (value == null) {
            return false;
        }


        return compare(StrUtil.format("${key{}{}}", symbol, param), Dict.create().set("key", Convert.toNumber(value)));

    }


    private Boolean compare(String symbol, Dict value) {
        return DataUtil.expression(symbol, value);
    }


    /**
     * 判断字符数组包含
     *
     * @param key 表单key
     * @return
     */
    public boolean selectHandler(String key, DelegateExecution execution, String param, String symbol) {
        return selectHandler(key, execution.getVariables(), param, symbol);
    }

    public boolean selectHandler(String key, Map<String, Object> execution, String param, String symbol) {
        List<SelectValue> paramObjList = JsonUtil.parseArray(EscapeUtil.unescape(param), SelectValue.class);
        List<String> paramList = paramObjList.stream().map(w -> w.getKey()).collect(Collectors.toList());
        Object value = execution.get(key);


        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EMPTY)) {

            return value == null || StrUtil.isBlankIfStr(value) || JsonUtil.toList(value).size() == 0;

        }
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EMPTY)) {

            return value != null && !StrUtil.isBlankIfStr(value) && JsonUtil.toList(value).size() > 0;

        }

        if (value == null) {
            return false;
        }
        List<SelectValue> list = JsonUtil.toList(SelectValue.class, value);
        return selectHandler(list.stream().map(w -> w.getKey()).collect(Collectors.toList()), paramList, symbol);
    }

    public boolean selectHandler(Object value, List<String> paramList, String symbol) {


        log.debug("表单值：value={}  symbol={}", JsonUtil.toJSONString(value), symbol);
        log.debug("条件  参数：{}", JsonUtil.toJSONString(paramList));

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EMPTY)) {

            return value == null || StrUtil.isBlankIfStr(value) || JsonUtil.toList(value).size() == 0;

        }
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EMPTY)) {

            return value != null && !StrUtil.isBlankIfStr(value) && JsonUtil.toList(value).size() > 0;

        }

        if (value == null || JsonUtil.toList(value).size() == 0) {
            return false;
        }

        List<String> valueList = JsonUtil.toList(String.class, value);

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EQUAL)) {
            //等于
            if (valueList.size() != paramList.size()) {
                return false;
            }
            paramList.removeAll(valueList);
            return paramList.isEmpty();


        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EQUAL)) {
            //不等于
            if (valueList.size() != paramList.size()) {
                return true;
            }
            paramList.removeAll(valueList);
            return !paramList.isEmpty();
        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.IN)) {
            //属于
            if (valueList.size() > paramList.size()) {
                return false;
            }


            valueList.removeAll(paramList);

            return valueList.isEmpty();
        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_IN)) {
            //不属于

            int size = valueList.size();

            valueList.removeAll(paramList);

            return !valueList.isEmpty();
        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.CONTAIN)) {
            //包含

            paramList.removeAll(valueList);

            return paramList.isEmpty();
        }


        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_CONTAIN)) {
            //不包含
            int size = paramList.size();
            paramList.removeAll(valueList);

            return paramList.size() == size;
        }
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.INTERSECTION)) {
            int size = paramList.size();
            paramList.removeAll(valueList);
            return paramList.size() < size;
        }
        return false;
    }

    /**
     * 处理表达式
     *
     * @param execution
     * @param uniqueId
     * @return
     */
    public boolean handle(DelegateExecution execution, String uniqueId) {
        String s = NodeDataStoreFactory.getInstance().get(uniqueId, uniqueId, execution.getTenantId());
        Condition condition = JsonUtil.parseObject(s, Condition.class);
        Map<String, Object> variables = execution.getVariables();
        return NodeExpressionResultStrategyFactory.handleSingleConditionResult(condition, variables, execution.getTenantId());
    }

    /**
     * 字符串判断
     *
     * @param key   表单key
     * @param param 参数
     * @return
     */
    public boolean stringHandler(String key, String param, DelegateExecution execution, String symbol) {

        return stringHandler(key, param, execution.getVariable(key), symbol);

    }

    /**
     * 判断所有的变量是否为null
     *
     * @param execution
     * @param keyArr
     * @return
     */
    public boolean isAllNull(DelegateExecution execution, String... keyArr) {
        Map<String, Object> variables = execution.getVariables(ListUtil.of(keyArr));
        for (String s : keyArr) {
            Object o = variables.get(s);
            if (o != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断所有的变量是否为false
     *
     * @param execution
     * @param keyArr
     * @return
     */
    public boolean isAllFalse(DelegateExecution execution, String... keyArr) {
        Map<String, Object> variables = execution.getVariables(ListUtil.of(keyArr));
        for (String s : keyArr) {

            Boolean bool = MapUtil.getBool(variables, s);
            if (bool == null) {
                return false;
            }

            if (bool) {
                return false;
            }
        }
        return true;
    }

    /**
     * 不是所有的都是true
     *
     * @param execution
     * @param keyArr
     * @return
     */
    public boolean isNotAllTrue(DelegateExecution execution, String... keyArr) {
        Map<String, Object> variables = execution.getVariables(ListUtil.of(keyArr));
        for (String s : keyArr) {

            Boolean bool = MapUtil.getBool(variables, s);
            if (bool == null) {
                return true;
            }

            if (!bool) {
                return true;
            }
        }
        return false;
    }

    /**
     * 没有一个是true
     *
     * @param execution
     * @param keyArr
     * @return
     */
    public boolean isAllNotTrue(DelegateExecution execution, String... keyArr) {
        Map<String, Object> variables = execution.getVariables(ListUtil.of(keyArr));
        for (String s : keyArr) {

            Boolean bool = MapUtil.getBool(variables, s);
            if (bool == null) {
                continue;
            }

            if (bool) {
                return false;
            }
        }
        return true;
    }


    public boolean stringHandler(String key, String param, Object value, String symbol) {

        log.debug("表单值：key={} value={}", key, JsonUtil.toJSONString(value));
        log.debug("条件  参数：{}", JsonUtil.toJSONString(param));

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EMPTY)) {

            return value == null || StrUtil.isBlankIfStr(value);

        }
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EMPTY)) {

            return value != null && !StrUtil.isBlankIfStr(value);

        }
        if (value == null) {
            return false;
        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EQUAL)) {

            return StrUtil.equals(value.toString(), param);

        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EQUAL)) {

            return !StrUtil.equals(value.toString(), param);

        }


        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.CONTAIN)) {

            return StrUtil.contains(value.toString(), param);

        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_CONTAIN)) {

            return !StrUtil.contains(value.toString(), param);

        }


        return false;
    }


    public boolean deptCompare(String key, String param, String symbol, DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        return deptCompare(param, symbol, variables.get(key), execution.getTenantId());
    }

    public boolean deptCompare(String param, String symbol, Object value, String tenantId) {
        param = EscapeUtil.unescape(param);


        String jsonString = JsonUtil.toJSONString(value);
        log.debug("表单值：  value={} symbol={}", jsonString, symbol);
        log.debug("条件  参数：{}", param);

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.EMPTY)) {

            return value == null || StrUtil.isBlankIfStr(value) || JsonUtil.toList(value).size() == 0;

        }
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_EMPTY)) {

            return value != null && !StrUtil.isBlankIfStr(value) && JsonUtil.toList(value).size() > 0;

        }

        if (value == null || JsonUtil.toList(value).size() == 0) {
            return false;
        }

        //表单值
        List<NodeUser> nodeUserDtoList = JsonUtil.parseArray(jsonString, NodeUser.class);
        if (CollUtil.isEmpty(nodeUserDtoList) || nodeUserDtoList.size() != 1) {
            return false;
        }
        // NodeUser nodeUserDto = nodeUserDtoList.get(0);

        //参数
        List<NodeUser> paramDeptList = JsonUtil.parseArray(param, NodeUser.class);
        List<String> deptIdList = paramDeptList.stream().map(NodeUser::getId).collect(Collectors.toList());

//        String deptId = nodeUserDto.getId();
        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.IN_CHILD)) {
            //属于同级及子级

            Set<String> queryDeptIdList=new HashSet<>();

            for (NodeUser nodeUser : nodeUserDtoList) {
                String deptId = nodeUser.getId();
                //查询所有的上级部门
                List<DeptDto> deptDtoList = BizHttpUtil.queryParentDeptList(deptId, tenantId).getData();
                List<String> collect = deptDtoList.stream().map(DeptDto::getId).collect(Collectors.toList());
                queryDeptIdList.addAll(collect);
            }
            int oldSize = deptIdList.size();
            deptIdList.removeAll(queryDeptIdList);
            return deptIdList.size() < oldSize;

        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_IN_CHILD)) {
            //notin_child

            Set<String> queryDeptIdList=new HashSet<>();

            for (NodeUser nodeUser : nodeUserDtoList) {
                String deptId = nodeUser.getId();
                List<DeptDto> deptDtoList = BizHttpUtil.queryParentDeptList(deptId, tenantId).getData();
                List<String> collect = deptDtoList.stream().map(DeptDto::getId).collect(Collectors.toList());

                queryDeptIdList.addAll(collect);
            }



            int oldSize = deptIdList.size();
            deptIdList.removeAll(queryDeptIdList);
            return deptIdList.size() >= oldSize;

        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.IN)) {
            //属于

            List<String> collect = nodeUserDtoList.stream().map(NodeUser::getId).collect(Collectors.toList());

           return !CollUtil.intersection(collect, deptIdList).isEmpty();

        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_IN)) {
            //不属于
            List<String> collect = nodeUserDtoList.stream().map(NodeUser::getId).collect(Collectors.toList());

            return CollUtil.intersection(collect, deptIdList).isEmpty();


        }


        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.CONTAIN)) {
            //包含
            Set<String> queryDeptIdSet=new HashSet<>();

            List<String> queryChildrenDeptIdList = nodeUserDtoList.stream().map(w -> w.getId()).collect(Collectors.toList());
            Map<String, List<DeptDto>> childrenDeptMap = BizHttpUtil.batchQueryChildDeptList(queryChildrenDeptIdList, tenantId).getData();
            for (Map.Entry<String, List<DeptDto>> entry : childrenDeptMap.entrySet()) {
                Set<String> collect = entry.getValue().stream().map(DeptDto::getId).collect(Collectors.toSet());
                queryDeptIdSet.addAll(collect);
            }


            deptIdList.removeAll(queryDeptIdSet);
            return deptIdList.isEmpty();
        }

        if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_CONTAIN)) {
            //不包含
            Set<String> queryDeptIdSet=new HashSet<>();


            List<String> queryChildrenDeptIdList = nodeUserDtoList.stream().map(w -> w.getId()).collect(Collectors.toList());
            Map<String, List<DeptDto>> childrenDeptMap = BizHttpUtil.batchQueryChildDeptList(queryChildrenDeptIdList, tenantId).getData();
            for (Map.Entry<String, List<DeptDto>> entry : childrenDeptMap.entrySet()) {
                Set<String> collect = entry.getValue().stream().map(DeptDto::getId).collect(Collectors.toSet());
                queryDeptIdSet.addAll(collect);
            }


            int size = deptIdList.size();
            deptIdList.removeAll(queryDeptIdSet);
            return deptIdList.size() == size;
        }


        return false;

    }


    /**
     * user判断
     *
     * @param key     表单key
     * @param param1  参数
     * @param userKey 比如年龄age
     * @return
     */
    public boolean userCompare(String key, String param1, String symbol, DelegateExecution execution, String userKey) {
        return userCompare(key, param1, symbol, execution.getVariables(), userKey, execution.getTenantId());
    }

    public boolean userCompare(String key, String param1, String symbol, Map<String, Object> execution,
                               String userKey, String tenantId) {

        param1 = EscapeUtil.unescape(param1);
        Object o = JsonUtil.parseArray(param1, Object.class).get(0);


        Object value = execution.get(key);

        String jsonString = JsonUtil.toJSONString(value);
        log.debug("表单值：key={} value={}   symbol={} userKey={} ", key, jsonString, symbol, userKey);
        log.debug("条件  参数：{}", o);


        if (StrUtil.equals(userKey, ProcessInstanceConstant.ConditionSymbol.EMPTY)) {

            return value == null || StrUtil.isBlankIfStr(value) || JsonUtil.toList(value).size() == 0;

        }
        if (StrUtil.equals(userKey, ProcessInstanceConstant.ConditionSymbol.NOT_EMPTY)) {

            return value != null && !StrUtil.isBlankIfStr(value) && JsonUtil.toList(value).size() > 0;

        }

        if (value == null || JsonUtil.toList(value).size() == 0) {
            return false;
        }
        if (value == null) {
            return false;
        }

        //表单值
        List<NodeUser> nodeUserDtoList = JsonUtil.parseArray(jsonString, NodeUser.class);
        if (CollUtil.isEmpty(nodeUserDtoList) || nodeUserDtoList.size() != 1) {
            return false;
        }
        NodeUser nodeUserDto = nodeUserDtoList.get(0);
        //获取用户值


        if (StrUtil.equals(ProcessInstanceConstant.ConditionSymbol.RANGE_USER, userKey)) {
            //参数
            List<NodeUser> paramDeptList = JsonUtil.parseArray(JsonUtil.toJSONString(o), NodeUser.class);

            List<String> userIdList = paramDeptList.stream().filter(w -> StrUtil.equals(w.getType(),
                    NodeUserTypeEnum.USER.getKey())).map(w -> (w.getId())).collect(Collectors.toList());


            if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.IN)) {
                //属于

                if (userIdList.contains(nodeUserDto.getId())) {
                    return true;
                }


                return false;
            }


            if (StrUtil.equals(symbol, ProcessInstanceConstant.ConditionSymbol.NOT_IN)) {
                //不属于

                if (userIdList.contains(nodeUserDto.getId())) {
                    return false;
                }

                return true;
            }

            return false;
        }
        //获取用户值


        if (StrUtil.equals(ProcessInstanceConstant.ConditionSymbol.RANGE_DEPT, userKey)) {
            //参数

            Map<String, Object> map = BizHttpUtil.queryUserInfo(nodeUserDto.getId(), tenantId).getData();

            UserDto userDto = BeanUtil.mapToBean(map, UserDto.class, CopyOptions.create());

            List<String> deptIdList = userDto.getDeptIdList();

            List<NodeUser> nodeUserList = new ArrayList<>();
//            for (String s : deptIdList) {
//                NodeUser n=new NodeUser();
//                n.setId(s);
//                n.setType(NodeUserTypeEnum.DEPT.getKey());
//                nodeUserList.add(n);
//            }


            return deptCompare(Convert.toStr(o), symbol, nodeUserList, tenantId);

        }
        if (StrUtil.equals(ProcessInstanceConstant.ConditionSymbol.ROLE, userKey)) {
            // 角色

            //参数
            List<NodeUser> paramList = JsonUtil.parseArray(JsonUtil.toJSONString(o), NodeUser.class);
            //当前用户
            String userId = nodeUserDto.getId();

            List<String> roleIdList = BizHttpUtil.loadRoleIdListByUserId(userId, tenantId);

            return selectHandler(roleIdList, paramList.stream().map(NodeUser::getId).collect(Collectors.toList()), symbol);

        }
        Map<String, Object> userInfo = BizHttpUtil.queryUserInfo(nodeUserDto.getId(), tenantId).getData();
        log.debug("查询到的用户信息:{}", JSONUtil.toJsonStr(userInfo));
        //查询变量属性
        Object userValue = userInfo.get(userKey);


        return false;

    }


}
