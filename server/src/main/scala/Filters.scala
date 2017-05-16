import javax.inject.Inject

import play.api.http.DefaultHttpFilters
import play.filters.gzip.GzipFilter

class Filters @Inject() (gzipFilter: GzipFilter, loggingFilter: LoggingFilter) extends DefaultHttpFilters(gzipFilter, loggingFilter)