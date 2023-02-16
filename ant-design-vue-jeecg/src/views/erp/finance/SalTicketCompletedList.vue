<template>
  <a-card :bordered='false'>

    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='searchQuery'>
        <a-row :gutter='24'>

          <a-col :xl='6' :lg='7' :md='8' :sm='24'>
            <a-form-item label='订单号'>
              <j-input placeholder='请输入订单号' v-model='queryParam.no'></j-input>
            </a-form-item>
          </a-col>
          <a-col :xl='6' :lg='7' :md='8' :sm='24'>
            <a-form-item label='订单日期'>
              <j-date placeholder="请选择开始" class="query-group-cust" v-model="queryParam.noDate_begin"></j-date>
              <span class="query-group-split-cust"></span>
              <j-date placeholder="请选择结束" class="query-group-cust" v-model="queryParam.noDate_end"></j-date>
            </a-form-item>
          </a-col>
          <template v-if='toggleSearchStatus'>
            <a-col :xl='6' :lg='7' :md='8' :sm='24'>
              <a-form-item label='业务员'>
                <j-input placeholder='请输入业务员' v-model='queryParam.operator'></j-input>
              </a-form-item>
            </a-col>
            <a-col :xl='6' :lg='7' :md='8' :sm='24'>
              <a-form-item label='客户'>
                <j-search-select-tag v-model="queryParam.customerId" :async="true" dict="bas_customer,aux_name,id" placeholder="请选择"/>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl='6' :lg='7' :md='8' :sm='24'>
            <span style='float: left;overflow: hidden;' class='table-page-search-submitButtons'>
              <a-button type='primary' @click='searchQuery' icon='search'>查询</a-button>
              <a-button type='primary' @click='searchReset' icon='reload' style='margin-left: 8px'>重置</a-button>
              <a @click='handleToggleSearch' style='margin-left: 8px'>
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'" />
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class='table-operator'>
<!--      <a-button @click='handleAdd' type='primary' icon='plus'>新增</a-button>-->
      <a-button type='primary' icon='download' @click="handleExportXls('订单')">导出</a-button>
      <a-dropdown v-if='selectedRowKeys.length > 0'>
        <a-menu slot='overlay'>
          <a-menu-item key='1' @click='batchDel'>
            <a-icon type='delete' />
            删除
          </a-menu-item>
        </a-menu>
        <a-button style='margin-left: 8px'> 批量操作
          <a-icon type='down' />
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class='ant-alert ant-alert-info' style='margin-bottom: 16px;'>
        <i class='anticon anticon-info-circle ant-alert-icon'></i> 已选择 <a
        style='font-weight: 600'>{{ selectedRowKeys.length }}</a>项
        <a style='margin-left: 24px' @click='onClearSelected'>清空</a>
      </div>

      <a-table
        ref='table'
        size='middle'
        bordered
        rowKey='id'
        :columns='columns'
        :dataSource='dataSource'
        :pagination='ipagination'
        :loading='loading'
        class='j-table-force-nowrap'
        :rowSelection='{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}'
        @change='handleTableChange'>

        <span slot='action' slot-scope='text, record'>
<!--          <a @click='handleEdit(record)'>编辑</a>-->
<!--          <a v-has="'SalTicket:check'" @click='handleApproval(record)' v-if='record.status === 0'>审核</a>-->

<!--          <a-divider v-has="'SalTicket:check'" v-if='record.status === 0' type='vertical' />-->
          <a-dropdown>
            <a class='ant-dropdown-link'>更多 <a-icon type='down' /></a>
            <a-menu slot='overlay'>
              <a-menu-item>
                <a-popconfirm title='确定删除吗?' @confirm='() => handleDelete(record.id)'>
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <salTicket-modal ref='modalForm' @ok='modalFormOk'></salTicket-modal>
    <common-approval-modal title='订单审核' check-url='/sale/salTicket/check' :ids='selectedRowKeys' ref='approvalModalForm' @close='modalFormOk'></common-approval-modal>
    <sal-ticket-delivery-modal ref='deliveryModalForm' @close='modalFormOk'></sal-ticket-delivery-modal>
    <sal-ticket-delivery-update-modal ref='deliveryUpdateModalForm' @close='modalFormOk'></sal-ticket-delivery-update-modal>
    <sal-ticket-receipts-modal ref='receiptsModalForm' @close='modalFormOk'></sal-ticket-receipts-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import SalTicketModal from './modules/SalTicketModal'
import CommonApprovalModal from './modules/CommonApprovalModal'
import SalTicketDeliveryModal from './modules/SalTicketDeliveryModal'
import SalTicketDeliveryUpdateModal from './modules/SalTicketDeliveryUpdateModal'
import SalTicketReceiptsModal from './modules/SalTicketReceiptsModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: 'SalTicketList',
  mixins: [JeecgListMixin],
  components: {
    SalTicketModal,
    CommonApprovalModal,
    SalTicketDeliveryModal,
    SalTicketReceiptsModal,
    SalTicketDeliveryUpdateModal
  },
  data() {
    return {
      description: '订单管理页面',
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function(t, r, index) {
            return parseInt(index) + 1
          }
        },
        {
          title: '订单号',
          align: 'center',
          width: 160,
          sorter: true,
          dataIndex: 'no'
        },
        {
          title: '订单日期',
          align: 'center',
          customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t),
          dataIndex: 'noDate'
        },
        {
          title: '源单号',
          align: 'center',
          width: 160,
          sorter: true,
          dataIndex: 'srcNo'
        },
        {
          title: '业务员',
          width:140,
          align: 'center',
          dataIndex: 'operator'
        },
        {
          title: '客户',
          align: 'center',
          width:140,
          dataIndex: 'customerId_dictText'
        },
        {
          title: '商品',
          align: 'center',
          dataIndex: 'materialId_dictText'
        },
        {
          title: '商品金额',
          align: 'center',
          width:100,
          dataIndex: 'materialAmt'
        },
        {
          title: '报价金额',
          align: 'center',
          width:100,
          dataIndex: 'quotedAmt'
        },
        {
          title: '商品数量',
          align: 'center',
          width:100,
          dataIndex: 'materialCount'
        },
        {
          title: '总金额',
          align: 'center',
          width:100,
          dataIndex: 'totalAmt'
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'remark'
        },

        {
          title: '结算方式',
          align: 'center',
          dataIndex: 'billingMethod_dictText'
        },
        {
          title: '付款方式',
          align: 'center',
          dataIndex: 'paymentsMethod_dictText'
        },
        {
          title: '是否开票',
          align: 'center',
          dataIndex: 'invoiceType_dictText'
        },
        {
          title: '审核人',
          align: 'center',
          dataIndex: 'approver_dictText'
        },
        {
          title: '审核结果',
          align: 'center',
          dataIndex: 'approvalResultType_dictText'
        },
        {
          title: '审核意见',
          align: 'center',
          dataIndex: 'approvalRemark'
        },
        {
          title:'创建时间',
          width:160,
          align:"center",
          dataIndex: 'createTime',
        },
        {
          title:'创建人',
          width:100,
          align:"center",
          dataIndex: 'createBy_dictText'
        },
        {
          title:'修改时间',
          width:160,
          align:"center",
          dataIndex: 'updateTime',
        },
        {
          title:'修改人',
          width:100,
          align:"center",
          dataIndex: 'updateBy_dictText'
        },
        {
          title: '操作',
          width: 100,
          dataIndex: 'action',
          align: 'center',
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/sale/salTicket/list/5',
        delete: '/sale/salTicket/delete',
        deleteBatch: '/sale/salTicket/deleteBatch',
        exportXlsUrl: 'sale/salTicket/exportXls/5',
        importExcelUrl: 'sale/salTicket/importExcel'
      }
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {
    handleApproval(record) {
      this.$refs.approvalModalForm.show([record.id]);
    },
    handleDelivery(record) {
      this.$refs.deliveryModalForm.show(record);
    },
    handleDeliveryUpdate(record) {
      this.$refs.deliveryUpdateModalForm.show(record);
    },
    handleReceipts(record) {
      this.$refs.receiptsModalForm.show(record);
    },
    handleApprovalBatch(ids) {
      if (ids.length === 0) {
        this.$message.warn("请勾选至少一条记录！");
      } else {
        this.$refs.approvalModalForm.show(ids);
      }
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>