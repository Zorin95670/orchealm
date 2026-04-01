export function usePagination() {
  function toPagination(pagination) {
    return {
      page: (pagination.page || 1) - 1,
      size: pagination.rowsPerPage || 5,
      sort: `${pagination.sortBy || 'updateDate'},${pagination.descending ? 'DESC' : 'ASC'}`,
    };
  }

  function toQuasarPagination(page) {
    return {
      page: page.page + 1,
      rowsPerPage: page.size,
      rowsNumber: page.totalElements,
      sortBy: page.sortBy,
      descending: page.direction === 'DESC'
    };
  }

  return {
    toPagination,
    toQuasarPagination,
  };
}
