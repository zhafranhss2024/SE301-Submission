package org.example.service;

import org.example.concurrency.LockFactory;
import org.example.model.ChamberSummary;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;


public class ReportGenerator {
    public static final Path ARCHIVE_DIR = Paths.get("archive");
    public static final Path REPORT_FILE = ARCHIVE_DIR.resolve("daily_report.txt");

    private final AlertManager alertManager;
    private final ChamberManager chamberManager;
    private final ResourceMonitor resourceMonitor;
    private final List<String> reports = new ArrayList<>();
    private final Lock reportLock = LockFactory.getLock(LockFactory.REPORT_LOCK);

    public ReportGenerator(
            AlertManager alertManager,
            ChamberManager chamberManager,
            ResourceMonitor resourceMonitor) {
        this.alertManager = alertManager;
        this.chamberManager = chamberManager;
        this.resourceMonitor = resourceMonitor;
    }

    public void generateDailyReport() {
        // Lock order: report, then alert/resource; those services never acquire reportLock.
        reportLock.lock();
        try {
            int generatedAlerts = alertManager.getTotalAlertsGenerated();
            int activeAlerts = alertManager.getTotalActiveAlerts();
            int waterUsage = resourceMonitor.getTotalWaterUsage();
            int energyUsage = resourceMonitor.getTotalEnergyUsage();

            System.out.println("===== DAILY REPORT =====");
            System.out.println("Generated Alerts: " + generatedAlerts);
            System.out.println("Active Alerts: " + activeAlerts);
            System.out.println("Water Usage: " + waterUsage + " L");
            System.out.println("Energy Usage: " + energyUsage + " kWh");
            reports.add("Daily Report");
        } finally {
            reportLock.unlock();
        }
    }

    public void archiveAlerts() {
        reportLock.lock();
        try {
            int archivedCount = alertManager.archiveActiveAlerts();
            if (archivedCount > 0) {
                reports.add("Archived " + archivedCount + " alerts");
                System.out.println("Archiving alerts... " + archivedCount + " moved to archive.");
            } else {
                reports.add("No alerts to archive");
                System.out.println("Archiving alerts... no active alerts to archive.");
            }
        } finally {
            reportLock.unlock();
        }
    }

    public List<String> getReportEntriesSnapshot() {
        reportLock.lock();
        try {
            return new ArrayList<>(reports);
        } finally {
            reportLock.unlock();
        }
    }

    public void saveArchiveReport() {
        try {
            Files.createDirectories(ARCHIVE_DIR);

            int generatedAlerts = alertManager.getTotalAlertsGenerated();
            int activeAlerts = alertManager.getTotalActiveAlerts();
            int waterUsage = resourceMonitor.getTotalWaterUsage();
            int energyUsage = resourceMonitor.getTotalEnergyUsage();
            List<String> archivedAlertsSnapshot = alertManager.getArchivedAlertsSnapshot();
            List<String> reportsSnapshot = getReportEntriesSnapshot();
            Map<String, ChamberSummary> summariesSnapshot = chamberManager.getChamberSummariesSnapshot();

            List<String> reportLines = new ArrayList<>();
            reportLines.add("===== DAILY REPORT =====");
            reportLines.add("Timestamp: " + new Date());
            reportLines.add("Generated Alerts: " + generatedAlerts);
            reportLines.add("Active Alerts: " + activeAlerts);
            reportLines.add("Water Usage: " + waterUsage + " L");
            reportLines.add("Energy Usage: " + energyUsage + " kWh");
            reportLines.add("Archived alerts: " + archivedAlertsSnapshot.size());
            reportLines.add("---");
            reportLines.addAll(reportsSnapshot);

            if (!summariesSnapshot.isEmpty()) {
                reportLines.add("--- chamber wise summary ---");
                TreeSet<String> sortedChambers = new TreeSet<>(summariesSnapshot.keySet());
                for (String chamber : sortedChambers) {
                    ChamberSummary summary = summariesSnapshot.get(chamber);
                    reportLines.add(summary.formatSummaryLine(chamber));
                }
            }

            if (!archivedAlertsSnapshot.isEmpty()) {
                reportLines.add("--- archived alerts ---");
                reportLines.addAll(archivedAlertsSnapshot);
            }

            Files.write(REPORT_FILE, reportLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);

            System.out.println("Archive report saved to: " + REPORT_FILE.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Could not save archive report: " + e.getMessage());
        }
    }
}
