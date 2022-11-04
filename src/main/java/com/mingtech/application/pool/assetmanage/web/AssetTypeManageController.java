package com.mingtech.application.pool.assetmanage.web;

import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.assetmanage.domain.AssetTypeManage;
import com.mingtech.application.pool.assetmanage.domain.AssetTypeManageHis;
import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.pool.common.util.BeanUtil;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AssetTypeManageController  extends BaseController {
    private static final Logger logger = Logger.getLogger(AssetTypeManageController.class);
    @Autowired
    private AssetTypeManageService assetTypeManageService;
    @Autowired
    private CacheUpdateService cacheUpdateService;
    /**
     *
     * 跳转到资产分类维护页面
     */
    @RequestMapping("/assetTypeAttention")
    public String queryBlackGrayList() {
        return "/pool/assetmanage/web/assetmanageController/queryAssetTypeManage";
    }
    /**
     * <p>
     * 方法名称: loadAssetTypeManageJSON|描述: 查询资产分类列表JSON
     * </p>
     */
    @RequestMapping("/queryAssetTypeManage")
    public void loadAssetTypeManageJSON(AssetTypeManage assetTypeManage) {
        try {
            String json = assetTypeManageService.queryAssetTypeManage(assetTypeManage, this.getCurrentUser(),this.getPage() );
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString(),e);
            logger.error(e.getMessage(),e);
        }
    }
    /**
     * <p>
     * 方法名称: txSaveAssetTypeManageList|描述: 保存资产分类修改
     * </p>
     */
    @RequestMapping("/saveAssetTypeManageList")
    public void txSaveAssetTypeManageList(AssetTypeManage assetTypeManage) {
        User user = this.getCurrentUser();
        try {
            //1、更新
            String id = assetTypeManage.getId() ;
            if(StringUtil.isEmpty(id)) {
                assetTypeManage.setId(null);
            }
            AssetTypeManage assetTypeManage1 = (AssetTypeManage) assetTypeManageService.load(id,AssetTypeManage.class);
            if(assetTypeManage.getAssignDelayDay()==assetTypeManage1.getAssignDelayDay() &&
                    assetTypeManage.getHolidayDelayType().equals(assetTypeManage1.getHolidayDelayType())){
                this.sendJSON("更新成功");
                logger.info("更新资产分类【"+assetTypeManage.getAssetType()+"】操作人【"+user.getLoginName()+"】！");
            }
            //历史数据存入资产分类历史表
            AssetTypeManageHis assetTypeManageHis = new AssetTypeManageHis();
            assetTypeManageHis.setAtManageId(assetTypeManage1.getId());
            BeanUtil.beanCopy(assetTypeManage1, assetTypeManageHis);

            assetTypeManage1.setHolidayDelayType(assetTypeManage.getHolidayDelayType());//节假日顺延类型
            assetTypeManage1.setAssignDelayDay(assetTypeManage.getAssignDelayDay());//设定顺延天数
            assetTypeManage1.setUpdateDate(DateUtils.getCurrDateTime());//更新时间
            assetTypeManage1.setUpdateUserId(user.getId());//更新人ID
            assetTypeManage1.setUpdateUserName(user.getLoginName());//更新人名称
            assetTypeManageService.txStore(assetTypeManage1);
            assetTypeManageService.txSaveStore(assetTypeManageHis);
            //2、对redis中资产缓存类型进行更新
            cacheUpdateService.txCacheUpdate(PublicStaticDefineTab.CACHE_DATA_TYPE_ASSET_TYPE_MANAGE);
            //3、启动存量业务期限配比额度重新计算调度任务进行额度重新试算

        } catch (Exception e) {
            logger.error("数据库操作失败"+e.getMessage());
            this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
            this.sendJSON("数据库操作失败："+e.getMessage());
        }
        this.sendJSON("更新成功");
        logger.info("更新资产分类【"+assetTypeManage.getAssetType()+"】操作人【"+user.getLoginName()+"】！");
    }
    /**
     * <p>
     * 方法名称: loadAssetTypeManageJSONHis|描述: 查询资产历史分类列表JSON
     * </p>
     */
    @RequestMapping("/queryAssetTypeManageHis")
    public void loadAssetTypeManageJSONHis(AssetTypeManage assetTypeManage){
        try {
            String json = assetTypeManageService.queryAssetTypeManageHis(assetTypeManage, this.getCurrentUser(),this.getPage() );
            if(!(StringUtil.isNotBlank(json))){
                json = RESULT_EMPTY_DEFAULT;
            }
            sendJSON(json);
        } catch (Exception e) {
            logger.error(e.toString(),e);
            logger.error(e.getMessage(),e);
        }
    }

}
