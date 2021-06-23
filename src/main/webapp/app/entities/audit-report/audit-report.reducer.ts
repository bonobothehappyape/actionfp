import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IAuditReport, defaultValue } from 'app/shared/model/audit-report.model';

export const ACTION_TYPES = {
  SEARCH_AUDITREPORTS: 'auditReport/SEARCH_AUDITREPORTS',
  FETCH_AUDITREPORT_LIST: 'auditReport/FETCH_AUDITREPORT_LIST',
  FETCH_AUDITREPORT: 'auditReport/FETCH_AUDITREPORT',
  CREATE_AUDITREPORT: 'auditReport/CREATE_AUDITREPORT',
  UPDATE_AUDITREPORT: 'auditReport/UPDATE_AUDITREPORT',
  PARTIAL_UPDATE_AUDITREPORT: 'auditReport/PARTIAL_UPDATE_AUDITREPORT',
  DELETE_AUDITREPORT: 'auditReport/DELETE_AUDITREPORT',
  RESET: 'auditReport/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IAuditReport>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type AuditReportState = Readonly<typeof initialState>;

// Reducer

export default (state: AuditReportState = initialState, action): AuditReportState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_AUDITREPORTS):
    case REQUEST(ACTION_TYPES.FETCH_AUDITREPORT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_AUDITREPORT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_AUDITREPORT):
    case REQUEST(ACTION_TYPES.UPDATE_AUDITREPORT):
    case REQUEST(ACTION_TYPES.DELETE_AUDITREPORT):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_AUDITREPORT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_AUDITREPORTS):
    case FAILURE(ACTION_TYPES.FETCH_AUDITREPORT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_AUDITREPORT):
    case FAILURE(ACTION_TYPES.CREATE_AUDITREPORT):
    case FAILURE(ACTION_TYPES.UPDATE_AUDITREPORT):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_AUDITREPORT):
    case FAILURE(ACTION_TYPES.DELETE_AUDITREPORT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_AUDITREPORTS):
    case SUCCESS(ACTION_TYPES.FETCH_AUDITREPORT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_AUDITREPORT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_AUDITREPORT):
    case SUCCESS(ACTION_TYPES.UPDATE_AUDITREPORT):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_AUDITREPORT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_AUDITREPORT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/audit-reports';
const apiSearchUrl = 'api/_search/audit-reports';

// Actions

export const getSearchEntities: ICrudSearchAction<IAuditReport> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_AUDITREPORTS,
  payload: axios.get<IAuditReport>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IAuditReport> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_AUDITREPORT_LIST,
  payload: axios.get<IAuditReport>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IAuditReport> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_AUDITREPORT,
    payload: axios.get<IAuditReport>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IAuditReport> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_AUDITREPORT,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IAuditReport> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_AUDITREPORT,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IAuditReport> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_AUDITREPORT,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IAuditReport> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_AUDITREPORT,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
