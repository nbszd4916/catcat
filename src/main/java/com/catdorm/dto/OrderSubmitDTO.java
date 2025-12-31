package com.catdorm.dto;

import java.util.List;

/**
 * 订单提交数据传输对象(DTO - Data Transfer Object)
 * 
 * 作用: 用于接收前端表单提交的订单和猫咪信息
 * 优点: 
 * 1. 清晰地定义前后端数据交互格式
 * 2. 解耦前端表单结构与后端实体类
 * 3. 便于数据验证和转换
 */
public class OrderSubmitDTO {
    
    // ========== 订单基本信息 ==========
    
    /** 联系方式(电话) */
    private String contact;
    
    /** 取猫地址 */
    private String pickAddress;
    
    /** 寄养开始时间 */
    private String startTime;
    
    /** 寄养时长(天) */
    private Integer duration;
    
    /** 是否需要接送服务: 0-不需要, 1-需要 */
    private Integer needPickup;
    
    /** 送猫地址(仅当needPickup=1时需要) */
    private String sendAddress;
    
    // ========== 猫咪信息列表 ==========
    
    /** 猫咪信息列表 */
    private List<CatDTO> cats;
    
    // ========== Getters and Setters ==========
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getPickAddress() {
        return pickAddress;
    }
    
    public void setPickAddress(String pickAddress) {
        this.pickAddress = pickAddress;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Integer getNeedPickup() {
        return needPickup;
    }
    
    public void setNeedPickup(Integer needPickup) {
        this.needPickup = needPickup;
    }
    
    public String getSendAddress() {
        return sendAddress;
    }
    
    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }
    
    public List<CatDTO> getCats() {
        return cats;
    }
    
    public void setCats(List<CatDTO> cats) {
        this.cats = cats;
    }
    
    /**
     * 猫咪信息DTO
     */
    public static class CatDTO {
        /** 猫咪名字 */
        private String catName;
        
        /** 品种 */
        private String breed;
        
        /** 体重(kg) */
        private Double weight;
        
        /** 年龄(月龄) */
        private Integer age;
        
        /** 图片URL */
        private String image;
        
        /** 备注 */
        private String remarks;
        
        // Getters and Setters
        
        public String getCatName() {
            return catName;
        }
        
        public void setCatName(String catName) {
            this.catName = catName;
        }
        
        public String getBreed() {
            return breed;
        }
        
        public void setBreed(String breed) {
            this.breed = breed;
        }
        
        public Double getWeight() {
            return weight;
        }
        
        public void setWeight(Double weight) {
            this.weight = weight;
        }
        
        public Integer getAge() {
            return age;
        }
        
        public void setAge(Integer age) {
            this.age = age;
        }
        
        public String getImage() {
            return image;
        }
        
        public void setImage(String image) {
            this.image = image;
        }
        
        public String getRemarks() {
            return remarks;
        }
        
        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
