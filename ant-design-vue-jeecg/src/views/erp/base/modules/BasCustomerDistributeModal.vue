<template>
  <a-drawer
    :title='title'
    :maskClosable='true'
    width='650'
    placement='right'
    :closable='true'
    @close='close'
    :visible='visible'
    style='overflow: auto;padding-bottom: 53px;'>

    <a-form>
      <a-form-model-item label='业务员' :labelCol='labelCol' :wrapperCol='wrapperCol' prop='operator' ref='operatorFmi'>
        <a-tooltip placement='bottom'>
          <j-select-user-by-dep v-model='operator' :multi='false' />
        </a-tooltip>
      </a-form-model-item>
      <a-form-item label='客户列表' :labelCol='labelCol' :wrapperCol='wrapperCol'>
        <a-list
          itemLayout='horizontal'
          :dataSource='customerList'
        >
          <a-list-item slot='renderItem' slot-scope='item, index' :key='index'>
            <template>
              <a slot='actions'>{{ item.name }}</a>
            </template>
          </a-list-item>
        </a-list>
      </a-form-item>
    </a-form>

    <div class='drawer-bootom-button'>
      <a-popconfirm title='确定放弃编辑？' @confirm='close' okText='确定' cancelText='取消'>
        <a-button style='margin-right: .8rem'>取消</a-button>
      </a-popconfirm>
      <!--      <a-button @click='handleSubmit(false)' type='primary' :loading='loading' ghost style='margin-right: 0.8rem'>仅保存-->
      <!--      </a-button>-->
      <a-button @click='handleSubmit(true)' type='primary' :loading='loading'>保存并关闭</a-button>
    </div>

  </a-drawer>

</template>
<script>
import { getAction, postAction } from '@/api/manage'

export default {
  name: 'BasCustomerDistributeModal',
  props: {
    customerIdList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      autoExpandParent: true,
      checkStrictly: true,
      title: '业务员分配',
      visible: false,
      loading: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      url: {
        customerList: '/base/basCustomer/listByIds',
        distribute: '/base/basCustomer/distribute'
      },
      operator: '',
      customerList: []
    }
  },
  methods: {
    show() {
      this.visible = true
    },
    close() {
      this.reset()
      this.$emit('close')
      this.visible = false
    },
    reset() {
      this.operator = ''
      this.customerList = []
      this.customerIdList = []
      this.loading = false
    },
    handleCancel() {
      this.close()
    },
    handleSubmit(exit) {
      let that = this
      const formData = new FormData();
      formData.append('operator', that.operator);
      formData.append('customerIds', that.customerIdList.join(','));
      that.loading = true
      console.log('请求参数：', formData)
      postAction(`${this.url.distribute}`, formData).then(res => {
        if (res.success) {
          that.$message.success(res.message)
          that.loading = false
          if (exit) {
            that.close()
          }
        } else {
          that.$message.error(res.message)
          that.loading = false
          if (exit) {
            that.close()
          }
        }
        this.loadData()
      })
    },
    loadData() {
      getAction(`${this.url.customerList}`, { ids: this.customerIdList.join(',') }).then(res => {
        console.log(res)
        if (res.success) {
          this.customerList = res.result
        }
      })
    }
  },
  watch: {
    visible() {
      if (this.visible) {
        this.loadData()
      }
    }
  }
}

</script>
<style lang='less' scoped>
.drawer-bootom-button {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-top: 1px solid #e8e8e8;
  padding: 10px 16px;
  text-align: right;
  left: 0;
  background: #fff;
  border-radius: 0 0 2px 2px;
}

</style>