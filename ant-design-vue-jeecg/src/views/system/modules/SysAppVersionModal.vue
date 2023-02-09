<template>
  <a-drawer
    :title="title"
    :width="800"
    placement="right"
    :closable="false"
    @close="close"
    :visible="visible">

    <a-spin :spinning="confirmLoading">
      <a-form-model ref="form" :model="model" :rules="validatorRules">

        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name" label="名称">
          <a-input placeholder="请输入名称" v-model="model.name" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="versionCode" label="版本号">
          <a-input placeholder="请输入版本号" v-model="model.versionCode" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="description" label="描述">
          <a-input placeholder="请输入描述" v-model="model.description" />
        </a-form-model-item>
        <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="link" label="下载链接">
          <a-input placeholder="请输入下载链接" v-model="model.link" />
        </a-form-model-item>

      </a-form-model>
    </a-spin>

    <div class="drawer-bootom-button">
      <a-button type="primary" @click="handleOk">确定</a-button>
      <a-button type="primary" @click="handleCancel">取消</a-button>
    </div>
  </a-drawer>
</template>

<script>
import { httpAction } from '@/api/manage'
import moment from "moment"

export default {
  name: "SysAppVersionModal",
  data () {
    return {
      title:"操作",
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },

      confirmLoading: false,
      validatorRules:{
      },
      url: {
        add: "/system/sysAppVersion/add",
        edit: "/system/sysAppVersion/edit",
      },
    }
  },
  created () {
  },
  methods: {
    add () {
      //初始化默认值
      this.edit({});
    },
    edit (record) {
      this.model = Object.assign({}, record);
      this.visible = true;
    },
    close () {
      this.$emit('close');
      this.visible = false;
      this.$refs.form.clearValidate();
    },
    handleOk () {
      const that = this;
      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true;
          let httpurl = '';
          let method = '';
          if(!this.model.id){
            httpurl+=this.url.add;
            method = 'post';
          }else{
            httpurl+=this.url.edit;
            method = 'put';
          }
          httpAction(httpurl,this.model,method).then((res)=>{
            if(res.success){
              that.$message.success(res.message);
              that.$emit('ok');
            }else{
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
            that.close();
          })
        }else{
          return false;
        }
      })
    },
    handleCancel () {
      this.close()
    },


  }
}
</script>

<style lang="less" scoped>
/**Button按钮间距*/
.ant-btn {
  margin-left: 30px;
  margin-bottom: 30px;
  float: right;
}
/**抽屉按钮样式*/
.drawer-bootom-button {
  position: absolute;
  bottom: -8px;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}
</style>