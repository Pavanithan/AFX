package com.appfuxion.campaignplatform.campaign.csv;

import java.util.List;

public record CsvImportResult(int importedCount, List<String> rowErrors) {
}
