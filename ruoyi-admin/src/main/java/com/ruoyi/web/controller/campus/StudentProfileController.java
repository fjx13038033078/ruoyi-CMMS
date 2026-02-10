package com.ruoyi.web.controller.campus;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.campus.domain.StudentProfile;
import com.ruoyi.campus.service.IStudentProfileService;

/**
 * 校园用户信息 操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/campus/profile")
public class StudentProfileController extends BaseController
{
    @Autowired
    private IStudentProfileService studentProfileService;

    /**
     * 查询校园用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:list')")
    @GetMapping("/list")
    public TableDataInfo list(StudentProfile studentProfile)
    {
        startPage();
        List<StudentProfile> list = studentProfileService.selectStudentProfileList(studentProfile);
        return getDataTable(list);
    }

    /**
     * 导出校园用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:export')")
    @Log(title = "校园用户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StudentProfile studentProfile)
    {
        List<StudentProfile> list = studentProfileService.selectStudentProfileList(studentProfile);
        ExcelUtil<StudentProfile> util = new ExcelUtil<StudentProfile>(StudentProfile.class);
        util.exportExcel(response, list, "校园用户信息数据");
    }

    /**
     * 根据主键获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:query')")
    @GetMapping(value = "/{profileId}")
    public AjaxResult getInfo(@PathVariable Long profileId)
    {
        return success(studentProfileService.selectStudentProfileById(profileId));
    }

    /**
     * 获取当前登录用户的学生信息
     */
    @GetMapping(value = "/mine")
    public AjaxResult getMyProfile()
    {
        Long userId = SecurityUtils.getUserId();
        return success(studentProfileService.selectProfileByUserId(userId));
    }

    /**
     * 根据用户ID获取校园信息
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:query')")
    @GetMapping(value = "/user/{userId}")
    public AjaxResult getInfoByUserId(@PathVariable Long userId)
    {
        return success(studentProfileService.selectStudentProfileByUserId(userId));
    }

    /**
     * 新增校园用户信息
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:add')")
    @Log(title = "校园用户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody StudentProfile studentProfile)
    {
        if (!studentProfileService.checkUserIdUnique(studentProfile))
        {
            return error("新增校园用户信息失败，该用户已注册");
        }
        if (studentProfile.getStudentNo() != null && !studentProfileService.checkStudentNoUnique(studentProfile))
        {
            return error("新增校园用户信息失败，学号已存在");
        }
        return toAjax(studentProfileService.insertStudentProfile(studentProfile));
    }

    /**
     * 修改校园用户信息
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:edit')")
    @Log(title = "校园用户信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody StudentProfile studentProfile)
    {
        if (studentProfile.getStudentNo() != null && !studentProfileService.checkStudentNoUnique(studentProfile))
        {
            return error("修改校园用户信息失败，学号已存在");
        }
        return toAjax(studentProfileService.updateStudentProfile(studentProfile));
    }

    /**
     * 删除校园用户信息
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:remove')")
    @Log(title = "校园用户信息", businessType = BusinessType.DELETE)
    @PostMapping("/remove/{profileIds}")
    public AjaxResult remove(@PathVariable Long[] profileIds)
    {
        return toAjax(studentProfileService.deleteStudentProfileByIds(profileIds));
    }

    /**
     * 审核校园用户认证
     */
    @PreAuthorize("@ss.hasPermi('campus:profile:audit')")
    @Log(title = "校园用户审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public AjaxResult audit(@RequestBody StudentProfile studentProfile)
    {
        return toAjax(studentProfileService.auditStudentProfile(studentProfile));
    }
}
