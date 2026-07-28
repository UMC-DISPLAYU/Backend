CREATE INDEX IDX_DISPLAY_SEARCH_CURSOR
    ON Display (status, displayId);

CREATE INDEX IDX_DISPLAY_CLOSING_SOON_CURSOR
    ON Display (status, endDate, displayId);

CREATE INDEX IDX_DISPLAY_GRADUATION_FILTER
    ON Display (status, displayType, displayId);

CREATE INDEX IDX_DISPLAY_MAP_BOUNDS
    ON Display (status, latitude, longitude, displayId);

CREATE INDEX IDX_DISPLAYIMAGE_MAIN_IMAGE
    ON DisplayImage (displayId, imageType, deletedAt, sortOrder);

CREATE INDEX IDX_DISPLAYFIELD_FIELD_FILTER
    ON DisplayField (field, displayId);
