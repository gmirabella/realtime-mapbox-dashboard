package com.project.dashboard.dao

import com.project.dashboard.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import javax.inject.Inject

@Repository
class DownloadDaoImpl : DownloadDao {

    private val log : Logger = LoggerFactory.getLogger(DownloadDaoImpl::class.java)

    @Inject private lateinit var jdbcTemplate: JdbcTemplate

    override fun getAll() : List<Download> {
        log.info("--- Getting all downloads ---")
        return jdbcTemplate.query("SELECT * FROM download", DownloadRowMapper())
    }

    override fun getByCountryAndDayPart(countryName: String, dayPart: DayPart): List<Download> {
        log.info("--- Getting all downloads by Country : <$countryName> and by DayPart : <${dayPart.name}>---")
        val sqlQuery = "SELECT * FROM download WHERE country_name = ? AND day_part = ?"

        val statement = PreparedStatementCreator { con ->
            con.prepareStatement(sqlQuery).apply {
                setString(1, countryName)
                setString(2, dayPart.name)
            }
        }
        return jdbcTemplate.query(statement, DownloadRowMapper())
    }

    override fun getByCountry(countryName: String): List<Download> {
        log.info("--- Getting downloads by Country : <$countryName> ---")
        val sqlQuery = "SELECT * FROM download WHERE country_name = ?"

        val statement = PreparedStatementCreator { con ->
            con.prepareStatement(sqlQuery).apply {
                setString(1, countryName)
            }
        }
        return jdbcTemplate.query(statement, DownloadRowMapper())
    }

    override fun getByDayPart(dayPart: DayPart): List<Download> {
        log.info("--- Getting downloads by DayPart : <${dayPart.name}> ---")
        val sqlQuery = "SELECT * FROM download WHERE day_part = ?"

        val statement = PreparedStatementCreator { con ->
            con.prepareStatement(sqlQuery).apply {
                setString(1, dayPart.name)
            }
        }
        return jdbcTemplate.query(statement, DownloadRowMapper())
    }

    override fun save(downloadedAt: Instant, position: Position, appId: AppId, countryName: String, dayPart: DayPart): Long {
        log.info("--- Saving download from <$appId> with lat, lon = <${position.lat}, ${position.lon}> ---")

        val sqlQuery = """
            INSERT INTO download (
            id,
            lat,
            lon,
            app_id,
            country_name,
            day_part,
            downloaded_at
            ) VALUES (nextval('sq_download'), ?, ?, ?, ?, ?, ?)
            returning id
        """

        val statement = PreparedStatementCreator { con ->
            con.prepareStatement(sqlQuery).apply {
                setBigDecimal(1, position.lat)
                setBigDecimal(2, position.lon)
                setString(3, appId.name)
                setString(4, countryName)
                setString(5, dayPart.name)
                setTimestamp(6, Timestamp.from(downloadedAt))
            }
        }

        return jdbcTemplate.query(statement, RowMapper{ rs, _ -> rs.getLong("id")}).first()
    }
}