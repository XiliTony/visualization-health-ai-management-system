package com.noitidart.api.ai.tools;

import com.noitidart.api.pojo.entity.HealthItem;
import com.noitidart.api.pojo.entity.HealthRecord;
import com.noitidart.api.pojo.entity.User;
import com.noitidart.api.service.HealthItemService;
import com.noitidart.api.service.HealthRecordService;
import com.noitidart.api.utils.AssertUtils;
import com.noitidart.api.utils.TokenUtils;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.invocation.InvocationParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI 添加健康项和健康记录工具类
 */
@Component
public class HealthDataTools {

    @Autowired
    private HealthItemService healthItemService;

    @Autowired
    private HealthRecordService healthRecordService;

    @Tool(name = "添加健康项", value = """
            1. 先向用户收集完整信息：健康项名、简介、单位、符号、正常值范围。
            2. 正常值范围必须是 最小值,最大值 格式，例如 36.0,37.0。
            3. 收集完整后自动检查是否已存在，不存在才让用户确认。
            4. 用户确认后再执行添加。
            """)
    public String addHealthItemByAI(
            @P("健康项") HealthItem healthItem,
            InvocationParameters parameters) {
        try {
            // 校验健康项名称不能为空
            if (healthItem.getName() == null || healthItem.getName().isBlank()) {
                return "请提供健康项名称，无法添加空名称的健康项";
            }

            // 从 InvocationParameters 中获得当前对话用户ID
            int userId = parameters.get("userId");

            // 查询是否已存在（公有同名 / 自己私有同名）
            boolean exists = queryHealthItem(healthItem.getName(), userId);
            if (exists) {
                return "❌ 该健康项已存在，不允许重复添加";
            }

            // 将ID添加到用户要添加的 healthItem中
            healthItem.setUserId(userId);

            // 因为用AI添加健康项的一定是用户，故健康项一定为私有
            healthItem.setIsGlobal(false);

            // 调用Service添加（自带完整校验）
            healthItemService.add(healthItem);

            // 返回成功信息
            return "✅ 健康项【" + healthItem.getName() + "】添加成功！";

        } catch (IllegalArgumentException e) {
            // 捕获异常，返回给AI友好提示
            return "❌ 添加失败：" + e.getMessage();
        } catch (Exception e) {
            // 未知错误兜底
            return "❌ 添加失败，系统异常：" + e.getMessage();
        }

    }

    @Tool(name = "添加健康记录", value = """
            1. 支持一次添加多条健康记录。
            2. 每条需要：健康项名称、记录的值，均不能为空。
            3. 对每一条，先检查健康项是否已存在。
            4. 不存在则告知用户并询问是否创建，不要直接添加。
            5. 全部存在后，展示信息给用户确认，确认后批量保存。
            """)
    public String addHealthRecordByAI(
            @P("健康记录列表") List<HealthRecord> healthRecordList,
            InvocationParameters parameters) {
        try {
            // 批量列表不能为空
            AssertUtils.notEmpty(healthRecordList, "请至少填写一条健康记录");

            int userId = parameters.get("userId");
            // 遍历检查每一条的健康项是否存在
            for (HealthRecord healthRecord : healthRecordList) {

                String healthItemName = healthRecord.getHealthItemName();
                // 判断是否已存在健康项
                if (!queryHealthItem(healthItemName, userId)) {
                    return "⚠️ 健康项【" + healthItemName + "】不存在，是否先创建？";
                }
                // 根据名称查询 healthItemId
                Integer healthItemId = healthItemService.getIdByName(healthItemName, userId);
                if(healthItemId == null) {
                    return "❌ 健康项【\" + healthItemName + \"】不存在，请先创建";
                }
                // 设置 healthItemId，数据库需要的
                healthRecord.setHealthItemId(healthItemId);
            }
            // 批量添加
            healthRecordService.addBatch(healthRecordList, userId);
            return "✅ 批量添加成功！共 " + healthRecordList.size() + " 条记录";
        } catch (Exception e) {
            return "❌ 批量添加失败：" + e.getMessage();
        }
    }

    /**
     * 内部工具方法：查询指定名称的健康项是否已存在（供添加健康项和健康记录时校验使用）
     * 查询规则：
     * 1. 系统内管理员创建的【公有健康项】存在同名 → 判定为存在
     * 2. 当前登录用户创建的【私有健康项】存在同名 → 判定为存在
     * 3. 其他用户创建的私有健康项同名 → 不算存在
     *
     * @param name 要查询的健康项名称
     * @return true=已存在（不可添加），false=不存在（可添加）
     */
    private boolean queryHealthItem(String name, int userId) {
        // 调用 service 层按业务规则查询
        return healthItemService.hasHealthItem(name, userId);
    }
}
