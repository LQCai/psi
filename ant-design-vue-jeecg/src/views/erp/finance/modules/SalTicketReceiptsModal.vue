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
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status" label="总金额">
        <span>{{ totalAmt }} 元</span>
      </a-form-model-item>
      <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" prop="status" label="收款金额">
        <a-input v-model="model.receiptAmt" />
      </a-form-model-item>

      <a-form-model-item label="结算方式" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="settleMethod">
        <j-search-select-tag v-model="model.settleMethod" dict="x_settle_method" />
      </a-form-model-item>

      <a-form-model-item label="收款资金账户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankAccountId">
        <j-search-select-tag v-model="model.bankAccountId" :async="true" dict="bas_bank_account,account_no,id" />
      </a-form-model-item>
    </a-form>

    <a-row :gutter="[24, 24]">
      <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
        <a-card title="收款记录" :loading="receiptList.loading" :bordered="false" :body-style="{ padding: '6' }">
          <template #extra><a @click="loadReceiptList(receiptList)"><a-icon type="sync" /></a></template>
          <a-row>
            <template v-if='receiptList.dataSource.length === 0'>
              <h3 style='text-align: center'>暂无数据</h3>
            </template>
            <template v-else>
              <h3>{{ `未收款金额: ${receiptList.unReceiptAmt} 元` }}</h3>
              <a-row v-for='rec in receiptList.dataSource' style='margin-bottom: 10px'>
                <a-col span='12'>
                  {{ `收款时间: ${rec.billDate}` }}
                </a-col>
                <a-col span='12'>
                  {{ `收款金额: ${rec.amt} 元` }}
                </a-col>
              </a-row>
            </template>
          </a-row>
        </a-card>
      </a-col>
    </a-row>

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
import { postAction, getAction } from '@/api/manage'

export default {
  name: 'SalTicketReceiptsModal',
  data() {
    return {
      autoExpandParent: true,
      checkStrictly: true,
      title: '收款确认',
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
        submit: '/sale/salTicket/confirmReceivable',
        receiptList: '/sale/salTicket/receiptList'
      },
      totalAmt: 0,
      model: {
        id: '',
        receiptAmt: 0,
        settleMethod: '',
        bankAccountId: ''
      },
      receiptList: {
        loading: false,
        dataSource: [],
        unReceiptAmt: 0
      },
    }
  },
  methods: {
    show(record) {
      this.model = {
        id: record.id
      }
      this.totalAmt = record.totalAmt
      this.visible = true
    },
    close() {
      this.reset()
      this.$emit('close')
      this.visible = false
    },
    reset() {
      this.totalAmt = 0
      this.model.receiptAmt = 0
      this.model.settleMethod = ''
      this.model.bankAccountId = ''
      this.loading = false
    },
    handleCancel() {
      this.close()
    },
    validate() {
      if (this.model.receiptAmt === '' || this.model.receiptAmt === 0 || this.model.receiptAmt > this.totalAmt) {
        this.$message.warning("请输入有效收款金额!")
        return false
      }
      if (this.model.settleMethod === '') {
        this.$message.warning("请选择付款方式!")
        return false
      }
      if (this.model.bankAccountId === '') {
        this.$message.warning("请选择收款资金账户!")
        return false
      }
      return true
    },
    handleSubmit(exit) {
      if (!this.validate()) {
        return
      }
      let that = this
      const data = this.model
      that.loading = true
      console.log('请求参数：', data)
      postAction(`${this.url.submit}`, data).then(res => {
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
    loadReceiptList(data) {
      data.loading = true;
      getAction(`${this.url.receiptList}?id=${this.model.id}`)
        .then(res => {
          data.dataSource =  res.result
          data.unReceiptAmt = this.totalAmt
            data.dataSource.map(it => {
            data.unReceiptAmt = data.unReceiptAmt - it.amt
          })
        })
        .finally(() => data.loading = false);
    },
    loadData() {
      this.loadReceiptList(this.receiptList)
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