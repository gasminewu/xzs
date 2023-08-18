import { post } from '@/utils/request'

export default {
  pageList: query => post('/api/admin/book/page', query),
  edit: query => post('/api/admin/book/edit', query),
  select: id => post('/api/admin/book/select/' + id),
  deletebook: id => post('/api/admin/book/delete/' + id)
}
