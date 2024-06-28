const parameter = (token, payload) => {
    return {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token,
            'Accept': '*/*',
            'Accept-Language': 'en-US,en;q=0.9',
            'Cookie': 'cid=5WKtlOpq2c-20240604; _ga=GA1.1.246269703.1717526946; ccpa-notice-viewed-02=true; oauthState=Y49VeQCF8mobIFwpskRUquszPwti0IPq4ZnttsTLvmI; oauthProvider=google; userSchoolId=U2Nob29sLTE1MjE=; userSchoolLegacyId=1521; userSchoolName=Drexel%20University; _ga_WET17VWCJ3=GS1.1.1719431599.29.1.1719432629.0.0.0',
            'Dnt': '1',
            'Host': 'www.ratemyprofessors.com',
            'Origin': 'https://www.ratemyprofessors.com',
            'Referer': 'https://www.ratemyprofessors.com/search/professors/1521?q=*',
            'Sec-Ch-Ua': '"Not/A)Brand";v="8", "Chromium";v="126", "Microsoft Edge";v="126"',
            'Sec-Ch-Ua-Mobile': '?0',
            'Sec-Ch-Ua-Platform': '"Windows"',
            'Sec-Fetch-Dest': 'empty',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Site': 'same-origin'
        },
        body: JSON.stringify(payload),
    };
};

const url = 'https://www.ratemyprofessors.com/graphql';

const payload = {
  query: `
    query SchoolSearchResultsPageQuery(
      $query: SchoolSearchQuery!
    ) {
      search: newSearch {
        ...SchoolSearchPagination_search_1ZLmLD
      }
    }

    fragment SchoolSearchPagination_search_1ZLmLD on newSearch {
      schools(query: $query, first: 8, after: "") {
        edges {
          cursor
          node {
            name
            ...SchoolCard_school
            id
            __typename
          }
        }
        pageInfo {
          hasNextPage
          endCursor
        }
        resultCount
      }
    }

    fragment SchoolCard_school on School {
      legacyId
      name
      numRatings
      avgRating
      avgRatingRounded
      ...SchoolCardHeader_school
      ...SchoolCardLocation_school
    }

    fragment SchoolCardHeader_school on School {
      name
    }

    fragment SchoolCardLocation_school on School {
      city
      state
    }
  `,
  variables: {
    query: {
      text: "drexel"
    }
  }
};

fetch(url, parameter("Basic dGVzdDp0ZXN0", payload))
  .then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
  })
  .then(data => {
    const bruh = data.data.search.schools.edges;
    console.log(bruh);
    const schools = data.data.search.schools.edges.map(edge => edge.node.name);
    console.log(schools);
  })
  .catch(error => {
    console.error('Error fetching data:', error);
    // Handle errors here
  });
