<template>
  <a-card :bordered='false'>

    <!-- 查询区域 -->
    <div class='table-page-search-wrapper'>
      <a-form layout='inline' @keyup.enter.native='searchQuery'>
        <a-row :gutter='24'>

          <a-col :xl='6' :lg='7' :md='8' :sm='24'>
            <a-form-item label='业务员'>
              <j-search-select-tag v-model="queryParam.operator" :async="true" dict="sys_user,realname,username" placeholder="请选择"/>
            </a-form-item>
          </a-col>
          <a-col :xl='6' :lg='7' :md='8' :sm='24'>
            <a-form-item label='客户'>
              <j-search-select-tag v-model="queryParam.customerId" :async="true" dict="bas_customer,aux_name,id" placeholder="请选择"/>
            </a-form-item>
          </a-col>
          <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="单据编号">-->
          <!--              <a-input placeholder="请输入单据编号" v-model="queryParam.billNo"></a-input>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
          <!--            <a-form-item label="单据日期">-->
          <!--              <a-input placeholder="请输入单据日期" v-model="queryParam.billDate"></a-input>-->
          <!--            </a-form-item>-->
          <!--          </a-col>-->
          <template v-if='toggleSearchStatus'>
            <!--          <a-col :xl="6" :lg="7" :md="8" :sm="24">-->
            <!--            <a-form-item label="subject">-->
            <!--              <a-input placeholder="请输入subject" v-model="queryParam.subject"></a-input>-->
            <!--            </a-form-item>-->
            <!--          </a-col>-->
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
<!--      <a-button type='primary' icon='download' @click="handleExportXls('询盘')">导出</a-button>-->
<!--      <a-upload name='file' :showUploadList='false' :multiple='false' :headers='tokenHeader' :action='importExcelUrl'-->
<!--                @change='handleImportExcel'>-->
<!--        <a-button type='primary' icon='import'>导入</a-button>-->
      </a-upload>
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
          <a @click='handleEdit(record)'>编辑</a>

          <a-divider type='vertical' />
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
    <salInquiry-modal ref='modalForm' @ok='modalFormOk'></salInquiry-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import SalInquiryModal from './modules/SalInquiryModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: 'SalInquiryList',
  mixins: [JeecgListMixin],
  components: {
    SalInquiryModal
  },
  data() {
    return {
      description: '询盘管理页面',
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
        // {
        //   title: '单据编号',
        //   align: 'center',
        //   dataIndex: 'billNo'
        // },
        // {
        //   title: '单据日期',
        //   align: 'center',
        //   dataIndex: 'billDate'
        // },
        // {
        //   title: 'subject',
        //   align: 'center',
        //   dataIndex: 'subject'
        // },
        {
          title: '业务员',
          width:160,
          align:"center",
          dataIndex: 'operator_dictText',
          sorter: true
        },
        {
          title:'客户',
          align:"left",
          width:100,
          dataIndex: 'customerId_dictText',
          ellipsis: true,
          sorter: true
        },
        {
          title: '商品',
          align: 'center',
          width: 160,
          dataIndex: 'materialId_dictText',
          sorter: true
        },
        {
          title: '报价金额',
          align: 'center',
          width:100,
          dataIndex: 'quotedAmt'
        },
        {
          title: '商品金额',
          align: 'center',
          width:100,
          dataIndex: 'materialAmt'
        },
        {
          title: '商品数量',
          align: 'center',
          width:100,
          dataIndex: 'materialCount'
        },
        // {
        //   title: '附件',
        //   align: 'center',
        //   dataIndex: 'attachment'
        // },
        {
          title: '备注',
          align: 'center',
          width:100,
          dataIndex: 'remark'
        },
        {
          title: '单据阶段',
          align: 'center',
          width:100,
          dataIndex: 'billStage'
        },
        {
          title: '审核人',
          align: 'center',
          width:100,
          dataIndex: 'approver'
        },
        {
          title: '下单意向',
          align: 'center',
          width:100,
          dataIndex: 'intention_dictText'
        },
        {
          title: '核批结果类型',
          align: 'center',
          width:100,
          dataIndex: 'approvalResultType'
        },
        {
          title: '核批意见',
          align: 'center',
          width:100,
          dataIndex: 'approvalRemark'
        },
        {
          title:'创建时间',
          width:100,
          align:"center",
          dataIndex: 'createTime',
          customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t)
        },
        {
          title:'创建人',
          width:100,
          align:"center",
          dataIndex: 'createBy_dictText'
        },
        {
          title:'修改时间',
          width:100,
          align:"center",
          dataIndex: 'updateTime',
          customRender: t => !t ? "" : (t.length > 10 ? t.substr(0, 10) : t)
        },
        {
          title:'修改人',
          width:100,
          align:"center",
          dataIndex: 'updateBy_dictText'
        },
        {
          title: '操作',
          width: 160,
          dataIndex: 'action',
          align: 'center',
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/sale/salInquiry/list',
        delete: '/sale/salInquiry/delete',
        deleteBatch: '/sale/salInquiry/deleteBatch',
        exportXlsUrl: 'sale/salInquiry/exportXls',
        importExcelUrl: 'sale/salInquiry/importExcel'
      }
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {}
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>